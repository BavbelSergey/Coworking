package com.example.coworking.service;

import com.example.coworking.dto.PaymentCreateDto;
import com.example.coworking.dto.PaymentDto;
import com.example.coworking.dto.PaymentUpdateDto;
import com.example.coworking.mapper.PaymentMapper;
import com.example.coworking.model.Booking;
import com.example.coworking.model.Payment;
import com.example.coworking.repository.BookingRepository;
import com.example.coworking.repository.PaymentRepository;
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
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final BookingRepository bookingRepository;
  private final PaymentMapper paymentMapper;

  public Page<PaymentDto> getAllPayments(Pageable pageable) {
    return paymentRepository.findAll(pageable).map(paymentMapper::toDto);
  }


  public PaymentDto getPaymentById(Long id) {
    Payment payment = paymentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    return paymentMapper.toDto(payment);
  }

  public PaymentDto getPaymentByBookingId(Long bookingId) {
    Payment payment = paymentRepository.findByBookingId(bookingId)
        .orElseThrow(() -> new RuntimeException("Payment not found for booking id: " + bookingId));
    return paymentMapper.toDto(payment);
  }

  @Transactional
  public PaymentDto createPayment(PaymentCreateDto createDto) {
    Booking booking = bookingRepository.findById(createDto.getBookingId()).orElseThrow(
        () -> new RuntimeException("Booking not found with id: " + createDto.getBookingId()));

    if (paymentRepository.existsByBookingId(createDto.getBookingId())) {
      throw new RuntimeException(
          "Payment already exists for booking id: " + createDto.getBookingId());
    }

    Payment payment = paymentMapper.toEntity(createDto, booking);
    Payment savedPayment = paymentRepository.save(payment);

    return paymentMapper.toDto(savedPayment);
  }

  @Transactional
  public PaymentDto updatePayment(Long id, PaymentUpdateDto updateDto) {
    Payment payment = paymentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

    paymentMapper.updateEntity(updateDto, payment);
    Payment updatedPayment = paymentRepository.save(payment);

    return paymentMapper.toDto(updatedPayment);
  }

  @Transactional
  public void deletePayment(Long id) {
    Payment payment = paymentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

    paymentRepository.delete(payment);
  }

  @Transactional
  public void deletePaymentByBookingId(Long bookingId) {
    if (!paymentRepository.existsByBookingId(bookingId)) {
      throw new RuntimeException("Payment not found for booking id: " + bookingId);
    }
    paymentRepository.deleteByBookingId(bookingId);
  }

  public List<PaymentDto> getUserPayments(Long userId) {
    return paymentMapper.toDtoList(paymentRepository.findByUserId(userId));
  }

  public List<PaymentDto> getWorkspacePayments(Long workspaceId) {
    return paymentMapper.toDtoList(paymentRepository.findByWorkspaceId(workspaceId));
  }

  public List<PaymentDto> getPaymentsInPeriod(LocalDateTime start, LocalDateTime end) {
    if (start.isAfter(end)) {
      throw new RuntimeException("Start time must be before end time");
    }
    return paymentMapper.toDtoList(paymentRepository.findByDateBetween(start, end));
  }

  public List<PaymentDto> getPaymentsByMethod(String method) {
    return paymentMapper.toDtoList(paymentRepository.findByPaymentMethod(method));
  }

  public Double getTotalAmountInPeriod(LocalDateTime start, LocalDateTime end) {
    Double total = paymentRepository.getTotalAmountBetween(start, end);
    return total != null ? total : 0.0;
  }

  public boolean isBookingPaid(Long bookingId) {
    return paymentRepository.existsByBookingId(bookingId);
  }
}