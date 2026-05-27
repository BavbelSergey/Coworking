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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService tests")
class PaymentServiceTest {

  @Mock
  private PaymentRepository paymentRepository;
  @Mock
  private BookingRepository bookingRepository;
  @Mock
  private PaymentMapper paymentMapper;

  @InjectMocks
  private PaymentService paymentService;

  private Payment payment;
  private PaymentDto paymentDto;
  private PaymentCreateDto createDto;
  private PaymentUpdateDto updateDto;
  private Booking booking;

  @BeforeEach
  void setUp() {
    booking = new Booking();
    booking.setId(1L);

    payment = new Payment();
    payment.setId(1L);
    payment.setAmount(BigDecimal.valueOf(1500.00));
    payment.setPaymentMethod("CARD");
    payment.setBooking(booking);

    paymentDto = new PaymentDto();
    paymentDto.setId(1L);
    paymentDto.setAmount(BigDecimal.valueOf(1500.00));
    paymentDto.setPaymentMethod("CARD");
    paymentDto.setBookingId(1L);

    createDto = new PaymentCreateDto();
    createDto.setAmount(BigDecimal.valueOf(1500.00));
    createDto.setPaymentMethod("CARD");
    createDto.setBookingId(1L);

    updateDto = new PaymentUpdateDto();
    updateDto.setAmount(BigDecimal.valueOf(2000.00));
    updateDto.setPaymentMethod("TRANSFER");
  }

  @AfterEach
  void tearDown() {
    reset(paymentRepository, bookingRepository, paymentMapper);
  }

  // ==================== getAllPayments ====================

  @Nested
  @DisplayName("getAllPayments")
  class GetAllPayments {

    @Test
    @DisplayName("Should return page of payments")
    void shouldReturnPage() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<Payment> page = new PageImpl<>(List.of(payment), pageable, 1);
      when(paymentRepository.findAll(pageable)).thenReturn(page);
      when(paymentMapper.toDto(any())).thenReturn(paymentDto);

      Page<PaymentDto> result = paymentService.getAllPayments(pageable);

      assertNotNull(result);
      assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when no payments")
    void shouldReturnEmptyPage() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<Payment> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
      when(paymentRepository.findAll(pageable)).thenReturn(emptyPage);

      Page<PaymentDto> result = paymentService.getAllPayments(pageable);

      assertNotNull(result);
      assertEquals(0, result.getTotalElements());
    }
  }

  // ==================== getPaymentById ====================

  @Nested
  @DisplayName("getPaymentById")
  class GetPaymentById {

    @Test
    @DisplayName("Should return payment when found")
    void shouldReturnPayment_WhenFound() {
      when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
      when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

      PaymentDto result = paymentService.getPaymentById(1L);

      assertNotNull(result);
      assertEquals(BigDecimal.valueOf(1500.00), result.getAmount());
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> paymentService.getPaymentById(99L));
      assertEquals(ErrorCode.PAYMENT_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== getPaymentByBookingId ====================

  @Nested
  @DisplayName("getPaymentByBookingId")
  class GetPaymentByBookingId {

    @Test
    @DisplayName("Should return payment when found")
    void shouldReturnPayment_WhenFound() {
      when(paymentRepository.findByBookingId(1L)).thenReturn(Optional.of(payment));
      when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

      PaymentDto result = paymentService.getPaymentByBookingId(1L);

      assertNotNull(result);
      assertEquals(1L, result.getBookingId());
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(paymentRepository.findByBookingId(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> paymentService.getPaymentByBookingId(99L));
      assertEquals(ErrorCode.PAYMENT_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== createPayment ====================

  @Nested
  @DisplayName("createPayment")
  class CreatePayment {

    @Test
    @DisplayName("Should create payment successfully")
    void shouldCreatePayment() {
      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
      when(paymentRepository.existsByBookingId(1L)).thenReturn(false);
      when(paymentMapper.toEntity(createDto, booking)).thenReturn(payment);
      when(paymentRepository.save(payment)).thenReturn(payment);
      when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

      PaymentDto result = paymentService.createPayment(createDto);

      assertNotNull(result);
      assertEquals(BigDecimal.valueOf(1500.00), result.getAmount());
    }

    @Test
    @DisplayName("Should throw NotFoundException when booking not found")
    void shouldThrowException_WhenBookingNotFound() {
      when(bookingRepository.findById(99L)).thenReturn(Optional.empty());
      createDto.setBookingId(99L);

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> paymentService.createPayment(createDto));
      assertEquals(ErrorCode.BOOKING_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when payment exists for booking")
    void shouldThrowException_WhenPaymentExists() {
      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
      when(paymentRepository.existsByBookingId(1L)).thenReturn(true);

      ConflictException ex = assertThrows(ConflictException.class,
          () -> paymentService.createPayment(createDto));
      assertEquals(ErrorCode.PAYMENT_EXISTS_FOR_BOOKING, ex.getErrorCode());
    }
  }

  // ==================== updatePayment ====================

  @Nested
  @DisplayName("updatePayment")
  class UpdatePayment {

    @Test
    @DisplayName("Should update payment successfully")
    void shouldUpdatePayment() {
      when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
      when(paymentRepository.save(payment)).thenReturn(payment);
      when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

      PaymentDto result = paymentService.updatePayment(1L, updateDto);

      assertNotNull(result);
      verify(paymentMapper).updateEntity(updateDto, payment);
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> paymentService.updatePayment(99L, updateDto));
      assertEquals(ErrorCode.PAYMENT_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== deletePayment ====================

  @Nested
  @DisplayName("deletePayment")
  class DeletePayment {

    @Test
    @DisplayName("Should delete payment successfully")
    void shouldDeletePayment() {
      when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

      paymentService.deletePayment(1L);

      verify(paymentRepository).delete(payment);
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> paymentService.deletePayment(99L));
      assertEquals(ErrorCode.PAYMENT_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== deletePaymentByBookingId ====================

  @Nested
  @DisplayName("deletePaymentByBookingId")
  class DeletePaymentByBookingId {

    @Test
    @DisplayName("Should delete payment by booking id successfully")
    void shouldDeletePaymentByBookingId() {
      when(paymentRepository.existsByBookingId(1L)).thenReturn(true);

      paymentService.deletePaymentByBookingId(1L);

      verify(paymentRepository).deleteByBookingId(1L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(paymentRepository.existsByBookingId(99L)).thenReturn(false);

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> paymentService.deletePaymentByBookingId(99L));
      assertEquals(ErrorCode.PAYMENT_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== getUserPayments ====================

  @Nested
  @DisplayName("getUserPayments")
  class GetUserPayments {

    @Test
    @DisplayName("Should return user payments")
    void shouldReturnUserPayments() {
      when(paymentRepository.findByUserId(1L)).thenReturn(List.of(payment));
      when(paymentMapper.toDtoList(any())).thenReturn(List.of(paymentDto));

      List<PaymentDto> result = paymentService.getUserPayments(1L);

      assertFalse(result.isEmpty());
      assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should return empty list when no payments")
    void shouldReturnEmptyList() {
      when(paymentRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
      when(paymentMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<PaymentDto> result = paymentService.getUserPayments(1L);

      assertTrue(result.isEmpty());
    }
  }

  // ==================== getWorkspacePayments ====================

  @Nested
  @DisplayName("getWorkspacePayments")
  class GetWorkspacePayments {

    @Test
    @DisplayName("Should return workspace payments")
    void shouldReturnWorkspacePayments() {
      when(paymentRepository.findByWorkspaceId(1L)).thenReturn(List.of(payment));
      when(paymentMapper.toDtoList(any())).thenReturn(List.of(paymentDto));

      List<PaymentDto> result = paymentService.getWorkspacePayments(1L);

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no payments")
    void shouldReturnEmptyList() {
      when(paymentRepository.findByWorkspaceId(1L)).thenReturn(Collections.emptyList());
      when(paymentMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<PaymentDto> result = paymentService.getWorkspacePayments(1L);

      assertTrue(result.isEmpty());
    }
  }

  // ==================== getPaymentsByMethod ====================

  @Nested
  @DisplayName("getPaymentsByMethod")
  class GetPaymentsByMethod {

    @Test
    @DisplayName("Should return payments by method")
    void shouldReturnPaymentsByMethod() {
      when(paymentRepository.findByPaymentMethod("CARD")).thenReturn(List.of(payment));
      when(paymentMapper.toDtoList(any())).thenReturn(List.of(paymentDto));

      List<PaymentDto> result = paymentService.getPaymentsByMethod("CARD");

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no payments")
    void shouldReturnEmptyList() {
      when(paymentRepository.findByPaymentMethod("CRYPTO")).thenReturn(Collections.emptyList());
      when(paymentMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<PaymentDto> result = paymentService.getPaymentsByMethod("CRYPTO");

      assertTrue(result.isEmpty());
    }
  }

  // ==================== getTotalAmountInPeriod ====================

  @Nested
  @DisplayName("getTotalAmountInPeriod")
  class GetTotalAmountInPeriod {

    @Test
    @DisplayName("Should return total amount")
    void shouldReturnTotalAmount() {
      LocalDateTime start = LocalDateTime.now().minusDays(7);
      LocalDateTime end = LocalDateTime.now();
      when(paymentRepository.getTotalAmountBetween(start, end)).thenReturn(1500.00);

      Double result = paymentService.getTotalAmountInPeriod(start, end);

      assertEquals(1500.00, result);
    }

    @Test
    @DisplayName("Should return 0.0 when null")
    void shouldReturnZero_WhenNull() {
      LocalDateTime start = LocalDateTime.now().minusDays(7);
      LocalDateTime end = LocalDateTime.now();
      when(paymentRepository.getTotalAmountBetween(start, end)).thenReturn(null);

      Double result = paymentService.getTotalAmountInPeriod(start, end);

      assertEquals(0.0, result);
    }
  }

  // ==================== isBookingPaid ====================

  @Nested
  @DisplayName("isBookingPaid")
  class IsBookingPaid {

    @Test
    @DisplayName("Should return true when booking has payment")
    void shouldReturnTrue() {
      when(paymentRepository.existsByBookingId(1L)).thenReturn(true);

      boolean result = paymentService.isBookingPaid(1L);

      assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when booking has no payment")
    void shouldReturnFalse() {
      when(paymentRepository.existsByBookingId(99L)).thenReturn(false);

      boolean result = paymentService.isBookingPaid(99L);

      assertFalse(result);
    }
  }
}