package com.example.coworking.controller;

import com.example.coworking.dto.BookingCreateDto;
import com.example.coworking.dto.BookingDto;
import com.example.coworking.dto.BookingUpdateDto;
import com.example.coworking.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class BookingController {

  private final BookingService bookingService;

  @GetMapping("/workspaces/")
  public ResponseEntity<Page<BookingDto>> getUserBookings(
      @RequestParam @Positive(message = "Max price must be positive") Long maxPrice,
      @RequestParam @Min(value = 0, message = "Min capacity cannot be negative") Long minCapacity,
      @PageableDefault(size = 20, sort = "status") Pageable pageable) {
    Page<BookingDto> bookings = bookingService.getUserBookings(maxPrice, minCapacity, pageable);
    return ResponseEntity.ok(bookings);
  }

  @GetMapping("/workspaces-native/")
  public ResponseEntity<Page<BookingDto>> getUserBookingsNative(
      @RequestParam @Positive(message = "Max price must be positive") Long maxPrice,
      @RequestParam @Min(value = 0, message = "Min capacity cannot be negative") Long minCapacity,
      @PageableDefault(size = 20, sort = "status") Pageable pageable) {
    Page<BookingDto> bookings = bookingService.getUserBookingsNative(maxPrice, minCapacity,
        pageable);
    return ResponseEntity.ok(bookings);
  }

  @GetMapping
  public ResponseEntity<Page<BookingDto>> getAllBookings(
      @PageableDefault(size = 20, sort = "status") Pageable pageable) {
    return ResponseEntity.ok(bookingService.getAllBookings(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookingDto> getBookingById(
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long id) {
    BookingDto booking = bookingService.getBookingById(id);
    return ResponseEntity.ok(booking);
  }

  @PostMapping
  public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody BookingCreateDto createDto) {
    BookingDto createdBooking = bookingService.createBooking(createDto);
    return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<BookingDto> updateBooking(
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long id,
      @Valid @RequestBody BookingUpdateDto updateDto) {
    BookingDto updatedBooking = bookingService.updateBooking(id, updateDto);
    return ResponseEntity.ok(updatedBooking);
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<BookingDto> cancelBooking(
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long id) {
    BookingDto cancelledBooking = bookingService.cancelBooking(id);
    return ResponseEntity.ok(cancelledBooking);
  }

  @PostMapping("/{id}/confirm")
  public ResponseEntity<BookingDto> confirmBooking(
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long id) {
    BookingDto confirmedBooking = bookingService.confirmBooking(id);
    return ResponseEntity.ok(confirmedBooking);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBooking(
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long id) {
    bookingService.deleteBooking(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/workspace/{workspaceId}")
  public ResponseEntity<List<BookingDto>> getWorkspaceBookings(
      @PathVariable @Positive(message =
          "Workspace ID must be a positive number") Long workspaceId) {
    List<BookingDto> bookings = bookingService.getWorkspaceBookings(workspaceId);
    return ResponseEntity.ok(bookings);
  }

  @GetMapping("/user/{userId}/active")
  public ResponseEntity<List<BookingDto>> getUserActiveBookings(
      @PathVariable @Positive(message = "User ID must be a positive number") Long userId) {
    List<BookingDto> bookings = bookingService.getUserActiveBookings(userId);
    return ResponseEntity.ok(bookings);
  }
}