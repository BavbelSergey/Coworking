package com.example.coworking.controller;

import com.example.coworking.dto.BookingCreateDto;
import com.example.coworking.dto.BookingDto;
import com.example.coworking.dto.BookingUpdateDto;
import com.example.coworking.service.BookingService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

  private final BookingService bookingService;

  @GetMapping("/user/{userId}")
  public ResponseEntity<Page<BookingDto>> getUserBookings(
      @PathVariable Long userId,
      @PageableDefault(size = 10, sort = "startTime") Pageable pageable
  ) {
    Page<BookingDto> bookings = bookingService.getUserBookings(userId, pageable);
    return ResponseEntity.ok(bookings);
  }

  @GetMapping("/user-native/{userId}")
  public ResponseEntity<Page<BookingDto>> getUserBookingsNative(
      @PathVariable Long userId,
      @PageableDefault(size = 10, sort = "start_Time") Pageable pageable
  ) {
    Page<BookingDto> bookings = bookingService.getUserBookingsNative(userId, pageable);
    return ResponseEntity.ok(bookings);
  }

  @GetMapping("/paged")
  public ResponseEntity<Page<BookingDto>> getAllBookings(
      @PageableDefault(size = 20, sort = "status") Pageable pageable) {
    return ResponseEntity.ok(bookingService.getAllBookings(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookingDto> getBookingById(@PathVariable Long id) {
    BookingDto booking = bookingService.getBookingById(id);
    return ResponseEntity.ok(booking);
  }

  @PostMapping
  public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody BookingCreateDto createDto) {
    BookingDto createdBooking = bookingService.createBooking(createDto);
    return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<BookingDto> updateBooking(@PathVariable Long id,
      @Valid @RequestBody BookingUpdateDto updateDto) {
    BookingDto updatedBooking = bookingService.updateBooking(id, updateDto);
    return ResponseEntity.ok(updatedBooking);
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<BookingDto> cancelBooking(@PathVariable Long id) {
    BookingDto cancelledBooking = bookingService.cancelBooking(id);
    return ResponseEntity.ok(cancelledBooking);
  }

  @PostMapping("/{id}/confirm")
  public ResponseEntity<BookingDto> confirmBooking(@PathVariable Long id) {
    BookingDto confirmedBooking = bookingService.confirmBooking(id);
    return ResponseEntity.ok(confirmedBooking);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
    bookingService.deleteBooking(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/workspace/{workspaceId}")
  public ResponseEntity<List<BookingDto>> getWorkspaceBookings(@PathVariable Long workspaceId) {
    List<BookingDto> bookings = bookingService.getWorkspaceBookings(workspaceId);
    return ResponseEntity.ok(bookings);
  }

  @GetMapping("/user/{userId}/active")
  public ResponseEntity<List<BookingDto>> getUserActiveBookings(@PathVariable Long userId) {
    List<BookingDto> bookings = bookingService.getUserActiveBookings(userId);
    return ResponseEntity.ok(bookings);
  }

  @GetMapping("/user/{userId}/upcoming")
  public ResponseEntity<List<BookingDto>> getUserUpcomingBookings(@PathVariable Long userId) {
    List<BookingDto> bookings = bookingService.getUserUpcomingBookings(userId);
    return ResponseEntity.ok(bookings);
  }

  @GetMapping("/user/{userId}/past")
  public ResponseEntity<List<BookingDto>> getUserPastBookings(@PathVariable Long userId) {
    List<BookingDto> bookings = bookingService.getUserPastBookings(userId);
    return ResponseEntity.ok(bookings);
  }

  @GetMapping("/available")
  public ResponseEntity<Boolean> checkAvailability(@RequestParam Long workspaceId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
    boolean isAvailable = bookingService.isWorkspaceAvailable(workspaceId, start, end);
    return ResponseEntity.ok(isAvailable);
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<BookingDto>> getBookingsByStatus(@PathVariable String status) {
    List<BookingDto> bookings = bookingService.getBookingsByStatus(status);
    return ResponseEntity.ok(bookings);
  }

  @GetMapping("/period")
  public ResponseEntity<List<BookingDto>> getBookingsInPeriod(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
    List<BookingDto> bookings = bookingService.getBookingsInPeriod(start, end);

    return ResponseEntity.ok(bookings);
  }

  @GetMapping("/{id}/cost")
  public ResponseEntity<Double> calculateBookingCost(@PathVariable Long id) {
    double cost = bookingService.calculateBookingCost(id);
    return ResponseEntity.ok(cost);
  }

  @PostMapping("/complete-expired")
  public ResponseEntity<String> completeExpiredBookings() {
    bookingService.completeExpiredBookings();
    return ResponseEntity.ok("Expired bookings have been completed");
  }
}