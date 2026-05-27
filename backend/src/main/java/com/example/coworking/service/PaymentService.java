package com.example.coworking.service;

import com.example.coworking.dto.PaymentCreateDto;
import com.example.coworking.dto.PaymentDto;
import com.example.coworking.dto.PaymentUpdateDto;
import com.example.coworking.exception.ConflictException;
import com.example.coworking.exception.ErrorCode;
import com.example.coworking.exception.NotFoundException;
import com.example.coworking.mapper.PaymentMapper;
import com.example.coworking.model.Booking;
import com.example.coworking.model.Payment;
import com.example.coworking.repository.BookingRepository;
import com.example.coworking.repository.PaymentRepository;
import java.math.BigDecimal;
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
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final BookingRepository bookingRepository;
  private final PaymentMapper paymentMapper;

  public Page<PaymentDto> getAllPayments(Pageable pageable) {
    log.debug("Fetching all payments with pageable: {}", pageable);
    Page<PaymentDto> result = paymentRepository.findAll(pageable).map(paymentMapper::toDto);
    log.debug("Found {} payments", result.getTotalElements());
    return result;
  }

  public PaymentDto getPaymentById(Long id) {
    log.debug("Fetching payment by id: {}", id);
    Payment payment = paymentRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Payment not found with id: {}", id);
          return new NotFoundException(ErrorCode.PAYMENT_NOT_FOUND);
        });
    log.info("Successfully fetched payment: id={}, amount={}, method={}",
        id, payment.getAmount(), payment.getPaymentMethod());
    return paymentMapper.toDto(payment);
  }

  public PaymentDto getPaymentByBookingId(Long bookingId) {
    log.debug("Fetching payment by booking id: {}", bookingId);
    Payment payment = paymentRepository.findByBookingId(bookingId)
        .orElseThrow(() -> {
          log.warn("Payment not found for booking id: {}", bookingId);
          return new NotFoundException(ErrorCode.PAYMENT_NOT_FOUND);
        });
    log.info("Successfully fetched payment for booking: bookingId={}, paymentId={}, amount={}",
        bookingId, payment.getId(), payment.getAmount());
    return paymentMapper.toDto(payment);
  }

  @Transactional
  public PaymentDto createPayment(PaymentCreateDto createDto) {
    log.info("Creating new payment: bookingId={}, amount={}, method={}",
        createDto.getBookingId(), createDto.getAmount(), createDto.getPaymentMethod());

    Booking booking = bookingRepository.findById(createDto.getBookingId())
        .orElseThrow(() -> {
          log.warn("Cannot create payment — booking not found: bookingId={}",
              createDto.getBookingId());
          return new NotFoundException(ErrorCode.BOOKING_NOT_FOUND);
        });

    if (paymentRepository.existsByBookingId(createDto.getBookingId())) {
      log.warn("Cannot create payment — booking already has a payment: bookingId={}",
          createDto.getBookingId());
      throw new ConflictException(ErrorCode.PAYMENT_EXISTS_FOR_BOOKING);
    }

    Payment payment = paymentMapper.toEntity(createDto, booking);
    Payment savedPayment = paymentRepository.save(payment);
    log.info("Successfully created payment: id={}, bookingId={}, amount={}, method={}",
        savedPayment.getId(), booking.getId(), savedPayment.getAmount(),
        savedPayment.getPaymentMethod());
    return paymentMapper.toDto(savedPayment);
  }

  @Transactional
  public PaymentDto updatePayment(Long id, PaymentUpdateDto updateDto) {
    log.info("Updating payment: id={}, updateData={}", id, updateDto);

    Payment payment = paymentRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Cannot update — payment not found: id={}", id);
          return new NotFoundException(ErrorCode.PAYMENT_NOT_FOUND);
        });

    String oldMethod = payment.getPaymentMethod();
    BigDecimal oldAmount = payment.getAmount();
    paymentMapper.updateEntity(updateDto, payment);
    Payment updatedPayment = paymentRepository.save(payment);
    log.info("Successfully updated payment: id={},"
            + " oldAmount={}, newAmount={}, oldMethod={}, newMethod={}",
        id, oldAmount, updatedPayment.getAmount(), oldMethod, updatedPayment.getPaymentMethod());
    return paymentMapper.toDto(updatedPayment);
  }

  @Transactional
  public void deletePayment(Long id) {
    log.info("Deleting payment: id={}", id);

    Payment payment = paymentRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Cannot delete — payment not found: id={}", id);
          return new NotFoundException(ErrorCode.PAYMENT_NOT_FOUND);
        });

    paymentRepository.delete(payment);
    log.info("Successfully deleted payment: id={}, bookingId={}, amount={}",
        id, payment.getBooking().getId(), payment.getAmount());
  }

  @Transactional
  public void deletePaymentByBookingId(Long bookingId) {
    log.info("Deleting payment by booking id: {}", bookingId);

    if (!paymentRepository.existsByBookingId(bookingId)) {
      log.warn("Cannot delete — payment not found for booking id: {}", bookingId);
      throw new NotFoundException(ErrorCode.PAYMENT_NOT_FOUND);
    }

    paymentRepository.deleteByBookingId(bookingId);
    log.info("Successfully deleted payment for booking id: {}", bookingId);
  }

  public List<PaymentDto> getUserPayments(Long userId) {
    log.debug("Fetching payments for user: {}", userId);
    List<PaymentDto> result = paymentMapper.toDtoList(paymentRepository.findByUserId(userId));
    log.debug("Found {} payments for user {}", result.size(), userId);
    return result;
  }

  public List<PaymentDto> getWorkspacePayments(Long workspaceId) {
    log.debug("Fetching payments for workspace: {}", workspaceId);
    List<PaymentDto> result = paymentMapper.toDtoList(
        paymentRepository.findByWorkspaceId(workspaceId));
    log.debug("Found {} payments for workspace {}", result.size(), workspaceId);
    return result;
  }

  public List<PaymentDto> getPaymentsByMethod(String method) {
    log.debug("Fetching payments by method: {}", method);
    List<PaymentDto> result = paymentMapper.toDtoList(
        paymentRepository.findByPaymentMethod(method));
    log.debug("Found {} payments with method '{}'", result.size(), method);
    return result;
  }

  public Double getTotalAmountInPeriod(LocalDateTime start, LocalDateTime end) {
    log.debug("Calculating total payment amount for period: {} – {}", start, end);
    Double total = paymentRepository.getTotalAmountBetween(start, end);
    Double result = total != null ? total : 0.0;
    log.info("Total payment amount for period {} – {}: {}", start, end, result);
    return result;
  }

  public boolean isBookingPaid(Long bookingId) {
    log.debug("Checking if booking is paid: bookingId={}", bookingId);
    boolean paid = paymentRepository.existsByBookingId(bookingId);
    log.debug("Booking {} is {}paid", bookingId, paid ? "" : "not ");
    return paid;
  }
}