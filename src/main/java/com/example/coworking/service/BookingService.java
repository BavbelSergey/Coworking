package com.example.coworking.service;

import com.example.coworking.cache.BookingSearchCache;
import com.example.coworking.dto.BookingCreateDto;
import com.example.coworking.dto.BookingDto;
import com.example.coworking.dto.BookingUpdateDto;
import com.example.coworking.exception.ConflictException;
import com.example.coworking.exception.ErrorCode;
import com.example.coworking.exception.NotFoundException;
import com.example.coworking.exception.UnprocessableContentException;
import com.example.coworking.mapper.BookingMapper;
import com.example.coworking.model.Booking;
import com.example.coworking.model.BookingStatus;
import com.example.coworking.model.User;
import com.example.coworking.model.Workspace;
import com.example.coworking.repository.BookingRepository;
import com.example.coworking.repository.UserRepository;
import com.example.coworking.repository.WorkspaceRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

  private final BookingRepository bookingRepository;
  private final UserRepository userRepository;
  private final WorkspaceRepository workspaceRepository;
  private final BookingMapper bookingMapper;
  private final BookingSearchCache bookingCache;

  public Page<BookingDto> getAllBookings(Pageable pageable) {
    log.debug("Fetching all bookings with pageable: {}", pageable);
    Page<BookingDto> result = bookingRepository.findAll(pageable).map(bookingMapper::toDto);
    log.debug("Found {} bookings", result.getTotalElements());
    return result;
  }

  public Page<BookingDto> getUserBookings(Long price, Long capacity, Pageable pageable) {
    log.debug("Fetching user bookings with price={}, capacity={}, pageable={}",
        price, capacity, pageable);

    Page<BookingDto> cachedBookings = bookingCache.get(price, capacity, pageable);
    if (cachedBookings == null) {
      log.debug("Cache miss — loading from database");
      Page<Booking> bookingsPage =
          bookingRepository.findBookingsByUserId(price, capacity, pageable);
      Page<BookingDto> result = bookingsPage.map(bookingMapper::toDto);
      bookingCache.put(price, capacity, pageable, result);
      log.info("Bookings cached: price={}, capacity={}", price, capacity);
      return result;
    }

    log.info("Bookings loaded from cache: price={}, capacity={}", price, capacity);
    return cachedBookings;
  }

  public Page<BookingDto> getUserBookingsNative(Long price, Long capacity, Pageable pageable) {
    log.debug("Fetching user bookings (native) with price={}, capacity={}, pageable={}",
        price, capacity, pageable);

    Page<BookingDto> cachedBookings = bookingCache.get(price, capacity, pageable);
    if (cachedBookings == null) {
      log.debug("Cache miss — loading from database (native query)");
      Page<Booking> bookingsPage =
          bookingRepository.findBookingsByUserIdNative(price, capacity, pageable);
      Page<BookingDto> result = bookingsPage.map(bookingMapper::toDto);
      bookingCache.put(price, capacity, pageable, result);
      log.info("Bookings cached (native): price={}, capacity={}", price, capacity);
      return result;
    }

    log.info("Bookings loaded from cache (native): price={}, capacity={}", price, capacity);
    return cachedBookings;
  }

  public BookingDto getBookingById(Long id) {
    log.debug("Fetching booking by id: {}", id);
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Booking not found with id: {}", id);
          return new NotFoundException(ErrorCode.BOOKING_NOT_FOUND);
        });
    log.info("Successfully fetched booking: id={}, status={}", id, booking.getStatus());
    return bookingMapper.toDto(booking);
  }

  @Transactional
  public BookingDto createBooking(BookingCreateDto createDto) {
    log.info("Creating new booking: userId={}, workspaceId={}, start={}, end={}",
        createDto.getUserId(), createDto.getWorkspaceId(),
        createDto.getStartTime(), createDto.getEndTime());

    Workspace workspace = workspaceRepository.findById(createDto.getWorkspaceId())
        .orElseThrow(() -> {
          log.warn("Workspace not found for booking: workspaceId={}",
              createDto.getWorkspaceId());
          return new NotFoundException(ErrorCode.BOOKING_NOT_FOUND);
        });

    List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
        workspace.getId(), createDto.getStartTime(), createDto.getEndTime());

    if (!conflictingBookings.isEmpty()) {
      log.warn("Workspace {} is not available for period {} – {} ({} conflicts)",
          workspace.getId(), createDto.getStartTime(), createDto.getEndTime(),
          conflictingBookings.size());
      throw new UnprocessableContentException(ErrorCode.WORKSPACE_NOT_AVAILABLE);
    }

    User user = userRepository.findById(createDto.getUserId())
        .orElseThrow(() -> {
          log.warn("User not found for booking: userId={}", createDto.getUserId());
          return new NotFoundException(ErrorCode.USER_NOT_FOUND);
        });

    Booking booking = bookingMapper.toEntity(createDto, user, workspace);
    Booking savedBooking = bookingRepository.save(booking);

    bookingCache.clear();
    log.info("Successfully created booking: id={}, userId={}, workspaceId={}, period={} – {}",
        savedBooking.getId(), user.getId(), workspace.getId(),
        savedBooking.getStartTime(), savedBooking.getEndTime());
    return bookingMapper.toDto(savedBooking);
  }

  @Transactional
  public BookingDto updateBooking(Long id, BookingUpdateDto updateDto) {
    log.info("Updating booking: id={}, updateData={}", id, updateDto);

    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Cannot update — booking not found: id={}", id);
          return new NotFoundException(ErrorCode.BOOKING_NOT_FOUND);
        });

    if (booking.getStatus() == BookingStatus.COMPLETED
        || booking.getStatus() == BookingStatus.CANCELLED) {
      log.warn("Cannot update booking id={} — status is {}", id, booking.getStatus());
      throw new ConflictException(ErrorCode.CAN_NOT_UPDATE);
    }

    if (updateDto.getStartTime() != null || updateDto.getEndTime() != null) {
      LocalDateTime newStart = updateDto.getStartTime() != null
          ? updateDto.getStartTime() : booking.getStartTime();
      LocalDateTime newEnd = updateDto.getEndTime() != null
          ? updateDto.getEndTime() : booking.getEndTime();

      log.debug("Checking conflicts for new period: {} – {}", newStart, newEnd);

      List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
          booking.getWorkspace().getId(), newStart, newEnd);
      conflictingBookings.removeIf(b -> b.getId().equals(id));

      if (!conflictingBookings.isEmpty()) {
        log.warn("Cannot update booking id={} — workspace not available for {} – {} ({} conflicts)",
            id, newStart, newEnd, conflictingBookings.size());
        throw new UnprocessableContentException(ErrorCode.WORKSPACE_NOT_AVAILABLE);
      }
    }

    String oldStatus = booking.getStatus().name();
    bookingMapper.updateEntity(updateDto, booking);
    Booking updatedBooking = bookingRepository.save(booking);

    bookingCache.clear();
    log.info("Successfully updated booking: id={}, oldStatus={}, newStatus={}",
        id, oldStatus, updatedBooking.getStatus());
    return bookingMapper.toDto(updatedBooking);
  }

  @Transactional
  public BookingDto cancelBooking(Long id) {
    log.info("Cancelling booking: id={}", id);

    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Cannot cancel — booking not found: id={}", id);
          return new NotFoundException(ErrorCode.BOOKING_NOT_FOUND);
        });

    if (booking.getStatus() == BookingStatus.COMPLETED
        || booking.getStatus() == BookingStatus.CANCELLED) {
      log.warn("Cannot cancel booking id={} — status is already {}", id, booking.getStatus());
      throw new ConflictException(ErrorCode.CAN_NOT_CANCEL);
    }

    BookingStatus oldStatus = booking.getStatus();
    booking.setStatus(BookingStatus.CANCELLED);
    Booking cancelledBooking = bookingRepository.save(booking);

    bookingCache.clear();
    log.info("Successfully cancelled booking: id={}, previousStatus={}",
        id, oldStatus);
    return bookingMapper.toDto(cancelledBooking);
  }

  @Transactional
  public void deleteBooking(Long id) {
    log.info("Deleting booking: id={}", id);

    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Cannot delete — booking not found: id={}", id);
          return new NotFoundException(ErrorCode.BOOKING_NOT_FOUND);
        });

    bookingCache.clear();
    bookingRepository.delete(booking);
    log.info("Successfully deleted booking: id={}", id);
  }

  public List<BookingDto> getWorkspaceBookings(Long workspaceId) {
    log.debug("Fetching bookings for workspace: {}", workspaceId);

    if (!workspaceRepository.existsById(workspaceId)) {
      log.warn("Workspace not found: {}", workspaceId);
      throw new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND);
    }

    List<BookingDto> result = bookingMapper.toDtoList(
        bookingRepository.findByWorkspaceId(workspaceId));
    log.debug("Found {} bookings for workspace {}", result.size(), workspaceId);
    return result;
  }

  public List<BookingDto> getUserActiveBookings(Long userId) {
    log.debug("Fetching active bookings for user: {}", userId);
    List<BookingDto> result = bookingMapper.toDtoList(
        bookingRepository.findByUserIdAndStatus(userId, BookingStatus.PENDING));
    log.debug("Found {} active bookings for user {}", result.size(), userId);
    return result;
  }

  @Transactional
  public BookingDto confirmBooking(Long id) {
    log.info("Confirming booking: id={}", id);

    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Cannot confirm — booking not found: id={}", id);
          return new NotFoundException(ErrorCode.BOOKING_NOT_FOUND);
        });

    if (booking.getStatus() != BookingStatus.PENDING) {
      log.warn("Cannot confirm booking id={} — current status is {} (expected PENDING)",
          id, booking.getStatus());
      throw new ConflictException(ErrorCode.CAN_NOT_CONFIRM);
    }

    booking.setStatus(BookingStatus.CONFIRMED);  // ← исправлено с PENDING на CONFIRMED
    Booking confirmedBooking = bookingRepository.save(booking);

    log.info("Successfully confirmed booking: id={}, userId={}, workspaceId={}",
        id, booking.getUser().getId(), booking.getWorkspace().getId());
    return bookingMapper.toDto(confirmedBooking);
  }
}