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
    return bookingRepository.findAll(pageable).map(bookingMapper::toDto);
  }

  public Page<BookingDto> getUserBookings(Long price, Long capacity, Pageable pageable) {
    Page<BookingDto> cachedBookings = bookingCache.get(price, capacity, pageable);
    if (cachedBookings == null) {

      Page<Booking> bookingsPage =
          bookingRepository.findBookingsByUserId(price, capacity, pageable);
      Page<BookingDto> result = bookingsPage.map(bookingMapper::toDto);
      bookingCache.put(price, capacity, pageable, result);
      log.info("Заказы занесены в кеш");
      return result;
    }
    log.info("Заказы получены из кеша");
    return cachedBookings;
  }

  public Page<BookingDto> getUserBookingsNative(Long price, Long capacity, Pageable pageable) {
    Page<BookingDto> cachedBookings = bookingCache.get(price, capacity, pageable);
    if (cachedBookings == null) {

      Page<Booking> bookingsPage =
          bookingRepository.findBookingsByUserIdNative(price, capacity, pageable);
      Page<BookingDto> result = bookingsPage.map(bookingMapper::toDto);
      bookingCache.put(price, capacity, pageable, result);
      log.info("Заказы сохранены в кеш");
      return result;
    }
    log.info("Заказы загружены из кеша");
    return cachedBookings;
  }

  public BookingDto getBookingById(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.BOOKING_NOT_FOUND));
    return bookingMapper.toDto(booking);
  }

  @Transactional
  public BookingDto createBooking(BookingCreateDto createDto) {

    Workspace workspace = workspaceRepository.findById(createDto.getWorkspaceId()).orElseThrow(
        () -> new NotFoundException(ErrorCode.BOOKING_NOT_FOUND));

    List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(workspace.getId(),
        createDto.getStartTime(), createDto.getEndTime());

    if (!conflictingBookings.isEmpty()) {
      throw new UnprocessableContentException(ErrorCode.WORKSPACE_NOT_AVAILABLE);
    }

    User user = userRepository.findById(createDto.getUserId()).orElseThrow(
        () -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    Booking booking = bookingMapper.toEntity(createDto, user, workspace);
    Booking savedBooking = bookingRepository.save(booking);

    bookingCache.clear();
    return bookingMapper.toDto(savedBooking);
  }

  @Transactional
  public BookingDto updateBooking(Long id, BookingUpdateDto updateDto) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.BOOKING_NOT_FOUND));

    if (booking.getStatus() == BookingStatus.COMPLETED
        || booking.getStatus() == BookingStatus.CANCELLED) {
      throw new RuntimeException("Cannot update completed or cancelled booking");
    }

    if (updateDto.getStartTime() != null || updateDto.getEndTime() != null) {
      LocalDateTime newStart =
          updateDto.getStartTime() != null ? updateDto.getStartTime() : booking.getStartTime();
      LocalDateTime newEnd =
          updateDto.getEndTime() != null ? updateDto.getEndTime() : booking.getEndTime();

      List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
          booking.getWorkspace().getId(), newStart, newEnd);

      conflictingBookings.removeIf(b -> b.getId().equals(id));

      if (!conflictingBookings.isEmpty()) {
        throw new UnprocessableContentException(ErrorCode.WORKSPACE_NOT_AVAILABLE);
      }
    }

    bookingMapper.updateEntity(updateDto, booking);
    bookingCache.clear();
    Booking updatedBooking = bookingRepository.save(booking);

    bookingCache.clear();
    return bookingMapper.toDto(updatedBooking);
  }

  @Transactional
  public BookingDto cancelBooking(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.BOOKING_NOT_FOUND));

    if (booking.getStatus() == BookingStatus.COMPLETED
        || booking.getStatus() == BookingStatus.CANCELLED) {
      throw new ConflictException(ErrorCode.CAN_NOT_CANCEL);
    }

    booking.setStatus(BookingStatus.CANCELLED);
    Booking cancelledBooking = bookingRepository.save(booking);
    bookingCache.clear();

    return bookingMapper.toDto(cancelledBooking);
  }

  @Transactional
  public void deleteBooking(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.BOOKING_NOT_FOUND));

    bookingCache.clear();
    bookingRepository.delete(booking);
  }

  public List<BookingDto> getWorkspaceBookings(Long workspaceId) {
    if (!workspaceRepository.existsById(workspaceId)) {
      throw new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND);
    }
    return bookingMapper.toDtoList(bookingRepository.findByWorkspaceId(workspaceId));
  }

  public List<BookingDto> getUserActiveBookings(Long userId) {
    return bookingMapper.toDtoList(
        bookingRepository.findByUserIdAndStatus(userId, BookingStatus.PENDING));
  }

  @Transactional
  public BookingDto confirmBooking(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.BOOKING_NOT_FOUND));

    if (booking.getStatus() != BookingStatus.PENDING) {
      throw new ConflictException(ErrorCode.CAN_NOT_CONFIRM);
    }

    booking.setStatus(BookingStatus.PENDING);
    Booking confirmedBooking = bookingRepository.save(booking);

    return bookingMapper.toDto(confirmedBooking);
  }
}