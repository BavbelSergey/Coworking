package com.example.coworking.controller;

import com.example.coworking.dto.PaymentCreateDto;
import com.example.coworking.dto.PaymentDto;
import com.example.coworking.dto.PaymentUpdateDto;
import com.example.coworking.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Validated
public class PaymentController {

  private final PaymentService paymentService;

  @GetMapping
  public ResponseEntity<Page<PaymentDto>> getAllPaymentsPaged(
      @PageableDefault(size = 20, sort = "date", direction = Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(paymentService.getAllPayments(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentDto> getPaymentById(
      @PathVariable @Positive(message = "Payment ID must be a positive number") Long id) {
    return ResponseEntity.ok(paymentService.getPaymentById(id));
  }

  @GetMapping("/booking/{bookingId}")
  public ResponseEntity<PaymentDto> getPaymentByBookingId(
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long bookingId) {
    return ResponseEntity.ok(paymentService.getPaymentByBookingId(bookingId));
  }

  @PostMapping
  public ResponseEntity<PaymentDto> createPayment(
      @Valid @RequestBody PaymentCreateDto createDto) {
    PaymentDto createdPayment = paymentService.createPayment(createDto);
    return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<PaymentDto> updatePayment(
      @PathVariable @Positive(message = "Payment ID must be a positive number") Long id,
      @Valid @RequestBody PaymentUpdateDto updateDto) {
    PaymentDto updatedPayment = paymentService.updatePayment(id, updateDto);
    return ResponseEntity.ok(updatedPayment);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePayment(
      @PathVariable @Positive(message = "Payment ID must be a positive number") Long id) {
    paymentService.deletePayment(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/booking/{bookingId}")
  public ResponseEntity<Void> deletePaymentByBookingId(
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long bookingId) {
    paymentService.deletePaymentByBookingId(bookingId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<PaymentDto>> getUserPayments(
      @PathVariable @Positive(message = "User ID must be a positive number") Long userId) {
    return ResponseEntity.ok(paymentService.getUserPayments(userId));
  }

  @GetMapping("/workspace/{workspaceId}")
  public ResponseEntity<List<PaymentDto>> getWorkspacePayments(
      @PathVariable @Positive(message =
          "Workspace ID must be a positive number") Long workspaceId) {
    return ResponseEntity.ok(paymentService.getWorkspacePayments(workspaceId));
  }

  @GetMapping("/method/{method}")
  public ResponseEntity<List<PaymentDto>> getPaymentsByMethod(
      @PathVariable @NotBlank(message = "Payment method cannot be blank") String method) {
    return ResponseEntity.ok(paymentService.getPaymentsByMethod(method));
  }

  @GetMapping("/total")
  public ResponseEntity<Double> getTotalAmountInPeriod(
      @RequestParam @NotNull(message = "Start date is required")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
      @RequestParam @NotNull(message = "End date is required")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
    return ResponseEntity.ok(paymentService.getTotalAmountInPeriod(start, end));
  }

  @GetMapping("/check/booking/{bookingId}")
  public ResponseEntity<Boolean> isBookingPaid(
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long bookingId) {
    return ResponseEntity.ok(paymentService.isBookingPaid(bookingId));
  }
}