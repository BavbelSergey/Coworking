package com.example.coworking.mapper;

import com.example.coworking.dto.PaymentCreateDto;
import com.example.coworking.dto.PaymentDto;
import com.example.coworking.dto.PaymentUpdateDto;
import com.example.coworking.model.Booking;
import com.example.coworking.model.Payment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

  public PaymentDto toDto(Payment payment) {
    if (payment == null) {
      return null;
    }

    PaymentDto dto = new PaymentDto();
    dto.setId(payment.getId());
    dto.setAmount(payment.getAmount());
    dto.setDate(payment.getDate());
    dto.setPaymentMethod(payment.getPaymentMethod());

    if (payment.getBooking() != null) {
      dto.setBookingId(payment.getBooking().getId());
      dto.setBookingStartTime(payment.getBooking().getStartTime());
      dto.setBookingEndTime(payment.getBooking().getEndTime());

      if (payment.getBooking().getUser() != null) {
        dto.setUserId(payment.getBooking().getUser().getId());
        dto.setUserName(payment.getBooking().getUser().getName());
      }

      if (payment.getBooking().getWorkspace() != null) {
        dto.setWorkspaceId(payment.getBooking().getWorkspace().getId());
        dto.setWorkspaceNumber(payment.getBooking().getWorkspace().getNumber());
      }
    }

    return dto;
  }

  public Payment toEntity(PaymentCreateDto createDto, Booking booking) {
    if (createDto == null) {
      return null;
    }

    Payment payment = new Payment();
    payment.setAmount(createDto.getAmount());
    payment.setPaymentMethod(createDto.getPaymentMethod());
    payment.setDate(LocalDateTime.now());
    payment.setBooking(booking);

    return payment;
  }

  public void updateEntity(PaymentUpdateDto updateDto, Payment payment) {
    if (updateDto == null || payment == null) {
      return;
    }

    if (updateDto.getAmount() != null) {
      payment.setAmount(updateDto.getAmount());
    }
    if (updateDto.getPaymentMethod() != null) {
      payment.setPaymentMethod(updateDto.getPaymentMethod());
    }
  }

  public List<PaymentDto> toDtoList(List<Payment> payments) {
    if (payments == null) {
      return new ArrayList<>();
    }
    return payments.stream()
        .map(this::toDto)
        .toList();
  }
}