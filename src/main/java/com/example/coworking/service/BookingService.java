package com.example.coworking.service;

import com.example.coworking.dto.BookingCreateDto;
import com.example.coworking.dto.BookingDto;
import com.example.coworking.dto.BookingUpdateDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

  private final BookingRepository bookingRepository;
  private final UserRepository userRepository;
  private final WorkspaceRepository workspaceRepository;
  private final BookingMapper bookingMapper;

  public Page<BookingDto> getAllBookings(Pageable pageable) {
    return bookingRepository.findAll(pageable).map(bookingMapper::toDto);
  }

  public BookingDto getBookingById(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    return bookingMapper.toDto(booking);
  }

  @Transactional
  public BookingDto createBooking(BookingCreateDto createDto) {
    if (createDto.getStartTime().isAfter(createDto.getEndTime())) {
      throw new RuntimeException("Start time must be before end time");
    }

    if (createDto.getStartTime().isBefore(LocalDateTime.now())) {
      throw new RuntimeException("Start time must be in the future");
    }

    Workspace workspace = workspaceRepository.findById(createDto.getWorkspaceId()).orElseThrow(
        () -> new RuntimeException("Workspace not found with id: " + createDto.getWorkspaceId()));

    List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(workspace.getId(),
        createDto.getStartTime(), createDto.getEndTime());

    if (!conflictingBookings.isEmpty()) {
      throw new RuntimeException("Workspace is not available for the selected time period");
    }

    User user = userRepository.findById(createDto.getUserId()).orElseThrow(
        () -> new RuntimeException("User not found with id: " + createDto.getUserId()));

    Booking booking = bookingMapper.toEntity(createDto, user, workspace);
    Booking savedBooking = bookingRepository.save(booking);

    return bookingMapper.toDto(savedBooking);
  }

  @Transactional
  public BookingDto updateBooking(Long id, BookingUpdateDto updateDto) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

    if (booking.getStatus() == BookingStatus.COMPLETED
        || booking.getStatus() == BookingStatus.CANCELLED) {
      throw new RuntimeException("Cannot update completed or cancelled booking");
    }

    if (updateDto.getStartTime() != null || updateDto.getEndTime() != null) {
      LocalDateTime newStart =
          updateDto.getStartTime() != null ? updateDto.getStartTime() : booking.getStartTime();
      LocalDateTime newEnd =
          updateDto.getEndTime() != null ? updateDto.getEndTime() : booking.getEndTime();

      if (newStart.isAfter(newEnd)) {
        throw new RuntimeException("Start time must be before end time");
      }

      List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
          booking.getWorkspace().getId(), newStart, newEnd);

      conflictingBookings.removeIf(b -> b.getId().equals(id));

      if (!conflictingBookings.isEmpty()) {
        throw new RuntimeException("Workspace is not available for the updated time period");
      }
    }

    bookingMapper.updateEntity(updateDto, booking);
    Booking updatedBooking = bookingRepository.save(booking);

    return bookingMapper.toDto(updatedBooking);
  }

  @Transactional
  public BookingDto cancelBooking(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

    if (booking.getStatus() == BookingStatus.COMPLETED) {
      throw new RuntimeException("Cannot cancel completed booking");
    }

    if (booking.getStatus() == BookingStatus.CANCELLED) {
      throw new RuntimeException("Booking is already cancelled");
    }

    if (booking.getStartTime().isBefore(LocalDateTime.now())) {
      throw new RuntimeException("Cannot cancel booking that has already started");
    }

    booking.setStatus(BookingStatus.CANCELLED);
    Booking cancelledBooking = bookingRepository.save(booking);

    return bookingMapper.toDto(cancelledBooking);
  }

  @Transactional
  public void deleteBooking(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

    bookingRepository.delete(booking);
  }

  public List<BookingDto> getUserBookings(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new RuntimeException("User not found with id: " + userId);
    }
    return bookingMapper.toDtoList(bookingRepository.findByUserId(userId));
  }

  public List<BookingDto> getWorkspaceBookings(Long workspaceId) {
    if (!workspaceRepository.existsById(workspaceId)) {
      throw new RuntimeException("Workspace not found with id: " + workspaceId);
    }
    return bookingMapper.toDtoList(bookingRepository.findByWorkspaceId(workspaceId));
  }

  public List<BookingDto> getUserActiveBookings(Long userId) {
    return bookingMapper.toDtoList(
        bookingRepository.findByUserIdAndStatus(userId, BookingStatus.PENDING));
  }

  public List<BookingDto> getUserUpcomingBookings(Long userId) {
    return bookingMapper.toDtoList(
        bookingRepository.findUpcomingBookings(userId, LocalDateTime.now()));
  }

  public List<BookingDto> getUserPastBookings(Long userId) {
    return bookingMapper.toDtoList(bookingRepository.findPastBookings(userId, LocalDateTime.now()));
  }

  public boolean isWorkspaceAvailable(Long workspaceId, LocalDateTime start, LocalDateTime end) {
    List<Booking> conflicting = bookingRepository.findConflictingBookings(workspaceId, start, end);
    return conflicting.isEmpty();
  }

  @Transactional
  public void completeExpiredBookings() {
    LocalDateTime now = LocalDateTime.now();
    List<Booking> activeBookings = bookingRepository.findByStatus(BookingStatus.PENDING);

    for (Booking booking : activeBookings) {
      if (booking.getEndTime().isBefore(now)) {
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);
      }
    }
  }

  public List<BookingDto> getBookingsByStatus(String status) {
    try {
      BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
      return bookingMapper.toDtoList(bookingRepository.findByStatus(bookingStatus));
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid status: " + status);
    }
  }

  public List<BookingDto> getBookingsInPeriod(LocalDateTime start, LocalDateTime end) {
    if (start.isAfter(end)) {
      throw new RuntimeException("Start time must be before end time");
    }
    return bookingMapper.toDtoList(bookingRepository.findByStartTimeBetween(start, end));
  }

  @Transactional
  public BookingDto confirmBooking(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

    if (booking.getStatus() != BookingStatus.PENDING) {
      throw new RuntimeException("Only pending bookings can be confirmed");
    }

    booking.setStatus(BookingStatus.PENDING);
    Booking confirmedBooking = bookingRepository.save(booking);

    return bookingMapper.toDto(confirmedBooking);
  }

  public double calculateBookingCost(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

    if (booking.getWorkspace() == null || booking.getWorkspace().getPricePerHour() == null) {
      throw new RuntimeException("Cannot calculate cost: workspace or price is missing");
    }

    long hours = java.time.Duration.between(booking.getStartTime(), booking.getEndTime()).toHours();
    if (hours == 0) {
      hours = 1;
    }

    return hours * booking.getWorkspace().getPricePerHour().doubleValue();
  }
}