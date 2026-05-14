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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService tests")
class BookingServiceTest {

  @Mock
  private BookingRepository bookingRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private WorkspaceRepository workspaceRepository;
  @Mock
  private BookingMapper bookingMapper;
  @Mock
  private BookingSearchCache bookingCache;

  @InjectMocks
  private BookingService bookingService;

  private User user;
  private Workspace workspace;
  private Booking booking;
  private BookingDto bookingDto;
  private BookingCreateDto createDto;
  private BookingUpdateDto updateDto;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setName("Иван");
    user.setEmail("ivan@example.com");

    workspace = new Workspace();
    workspace.setId(1L);
    workspace.setName("Workspace 101");

    booking = new Booking();
    booking.setId(1L);
    booking.setUser(user);
    booking.setWorkspace(workspace);
    booking.setStartDate(LocalDate.now().plusDays(1));
    booking.setEndDate(LocalDate.now().plusDays(1).plusDays(7));
    booking.setStatus(BookingStatus.PENDING);
    booking.setCreatedAt(LocalDateTime.now());

    bookingDto = new BookingDto();
    bookingDto.setId(1L);
    bookingDto.setUserId(1L);
    bookingDto.setWorkspaceId(1L);
    bookingDto.setStatus(BookingStatus.PENDING);

    createDto = new BookingCreateDto();
    createDto.setUserId(1L);
    createDto.setWorkspaceId(1L);
    createDto.setStartDate(LocalDate.now().plusDays(1));
    createDto.setEndDate(LocalDate.now().plusDays(1).plusDays(7));

    updateDto = new BookingUpdateDto();
    updateDto.setStartDate(LocalDate.now().plusDays(2));
    updateDto.setEndDate(LocalDate.now().plusDays(2).plusDays(7));
  }

  // ==================== getBookingById ====================

  @Nested
  @DisplayName("getBookingById")
  class GetBookingById {

    @Test
    @DisplayName("Should return booking when found")
    void shouldReturnBooking_WhenFound() {
      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
      when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

      BookingDto result = bookingService.getBookingById(1L);

      assertNotNull(result);
      assertEquals(1L, result.getId());
      verify(bookingRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when booking not found")
    void shouldThrowException_WhenNotFound() {
      when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> bookingService.getBookingById(99L));
      assertEquals(ErrorCode.BOOKING_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== createBooking ====================

  @Nested
  @DisplayName("createBooking")
  class CreateBooking {

    @Test
    @DisplayName("Should create booking successfully")
    void shouldCreateBooking() {
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(bookingRepository.findConflictingBookings(anyLong(), any(), any()))
          .thenReturn(Collections.emptyList());
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(bookingMapper.toEntity(any(), any(), any())).thenReturn(booking);
      when(bookingRepository.save(any())).thenReturn(booking);
      when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

      BookingDto result = bookingService.createBooking(createDto);

      assertNotNull(result);
      verify(bookingCache).clear();
      verify(bookingRepository).save(any());
    }

    @Test
    @DisplayName("Should throw UnprocessableContentException when workspace not available")
    void shouldThrowException_WhenWorkspaceNotAvailable() {
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(bookingRepository.findConflictingBookings(anyLong(), any(), any()))
          .thenReturn(List.of(new Booking()));

      assertThrows(UnprocessableContentException.class,
          () -> bookingService.createBooking(createDto));
    }

    @Test
    @DisplayName("Should throw NotFoundException when user not found")
    void shouldThrowException_WhenUserNotFound() {
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(bookingRepository.findConflictingBookings(anyLong(), any(), any()))
          .thenReturn(Collections.emptyList());
      when(userRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class,
          () -> bookingService.createBooking(createDto));
    }

    @Test
    @DisplayName("Should throw NotFoundException when workspace not found")
    void shouldThrowException_WhenWorkspaceNotFound() {
      when(workspaceRepository.findById(1L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> bookingService.createBooking(createDto));
      assertEquals(ErrorCode.BOOKING_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== updateBooking ====================

  @Nested
  @DisplayName("updateBooking")
  class UpdateBooking {

    @Test
    @DisplayName("Should update booking successfully with new times")
    void shouldUpdateBookingSuccessfully() {
      Workspace ws = new Workspace();
      ws.setId(1L);
      booking.setWorkspace(ws);

      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
      when(bookingRepository.findConflictingBookings(anyLong(), any(), any()))
          .thenReturn(Collections.emptyList());
      when(bookingRepository.save(any())).thenReturn(booking);
      when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

      BookingDto result = bookingService.updateBooking(1L, updateDto);

      assertNotNull(result);
      verify(bookingCache).clear();
    }

    @Test
    @DisplayName("Should throw ConflictException when booking is CANCELLED")
    void shouldThrowException_WhenCancelled() {
      booking.setStatus(BookingStatus.CANCELLED);
      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

      ConflictException ex = assertThrows(ConflictException.class,
          () -> bookingService.updateBooking(1L, updateDto));
      assertEquals(ErrorCode.CAN_NOT_UPDATE, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when booking is COMPLETED")
    void shouldThrowException_WhenCompleted() {
      booking.setStatus(BookingStatus.COMPLETED);
      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

      ConflictException ex = assertThrows(ConflictException.class,
          () -> bookingService.updateBooking(1L, updateDto));
      assertEquals(ErrorCode.CAN_NOT_UPDATE, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw NotFoundException when booking not found")
    void shouldThrowException_WhenNotFound() {
      when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> bookingService.updateBooking(99L, updateDto));
      assertEquals(ErrorCode.BOOKING_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should update only status when no time provided")
    void shouldUpdateOnlyStatus() {
      BookingUpdateDto statusOnlyDto = new BookingUpdateDto();
      statusOnlyDto.setStatus(BookingStatus.CONFIRMED);

      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
      when(bookingRepository.save(any())).thenReturn(booking);
      when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

      BookingDto result = bookingService.updateBooking(1L, statusOnlyDto);

      assertNotNull(result);
      verify(bookingRepository, never()).findConflictingBookings(anyLong(), any(), any());
      verify(bookingCache).clear();
    }

    @Test
    @DisplayName("Should keep old startTime when only endTime provided")
    void shouldKeepOldStartTime() {
      LocalDate oldStart = booking.getStartDate();
      BookingUpdateDto endOnlyDto = new BookingUpdateDto();
      endOnlyDto.setEndDate(LocalDate.now().plusDays(3));

      Workspace ws = new Workspace();
      ws.setId(1L);
      booking.setWorkspace(ws);

      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
      when(bookingRepository.findConflictingBookings(anyLong(), any(), any()))
          .thenReturn(Collections.emptyList());
      when(bookingRepository.save(any())).thenReturn(booking);
      when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

      bookingService.updateBooking(1L, endOnlyDto);

      verify(bookingRepository).findConflictingBookings(eq(1L), eq(oldStart), any());
    }

    @Test
    @DisplayName("Should keep old endTime when only startTime provided")
    void shouldKeepOldEndTime() {
      LocalDate oldEnd = booking.getEndDate();
      BookingUpdateDto startOnlyDto = new BookingUpdateDto();
      startOnlyDto.setStartDate(LocalDate.now().plusDays(1).plusDays(2));

      Workspace ws = new Workspace();
      ws.setId(1L);
      booking.setWorkspace(ws);

      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
      when(bookingRepository.findConflictingBookings(anyLong(), any(), any()))
          .thenReturn(Collections.emptyList());
      when(bookingRepository.save(any())).thenReturn(booking);
      when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

      bookingService.updateBooking(1L, startOnlyDto);

      verify(bookingRepository).findConflictingBookings(eq(1L), any(), eq(oldEnd));
    }

    @Test
    @DisplayName("Should throw UnprocessableContentException when new time conflicts")
    void shouldThrowException_WhenNewTimeConflicts() {
      Booking conflictingBooking = new Booking();
      conflictingBooking.setId(99L);
      Workspace ws = new Workspace();
      ws.setId(1L);
      booking.setWorkspace(ws);

      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
      when(bookingRepository.findConflictingBookings(anyLong(), any(), any()))
          .thenReturn(List.of(conflictingBooking));

      assertThrows(UnprocessableContentException.class,
          () -> bookingService.updateBooking(1L, updateDto));
    }
  }

  // ==================== cancelBooking ====================

  @Nested
  @DisplayName("cancelBooking")
  class CancelBooking {

    @Test
    @DisplayName("Should cancel booking successfully")
    void shouldCancelBooking() {
      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
      when(bookingRepository.save(any())).thenReturn(booking);
      when(bookingMapper.toDto(any())).thenReturn(bookingDto);

      BookingDto result = bookingService.cancelBooking(1L);

      assertNotNull(result);
      assertEquals(BookingStatus.CANCELLED, booking.getStatus());
      verify(bookingCache).clear();
    }

    @Test
    @DisplayName("Should throw ConflictException when already CANCELLED")
    void shouldThrowException_WhenAlreadyCancelled() {
      booking.setStatus(BookingStatus.CANCELLED);
      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

      ConflictException ex = assertThrows(ConflictException.class,
          () -> bookingService.cancelBooking(1L));
      assertEquals(ErrorCode.CAN_NOT_CANCEL, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when COMPLETED")
    void shouldThrowException_WhenCompleted() {
      booking.setStatus(BookingStatus.COMPLETED);
      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

      ConflictException ex = assertThrows(ConflictException.class,
          () -> bookingService.cancelBooking(1L));
      assertEquals(ErrorCode.CAN_NOT_CANCEL, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw NotFoundException when booking not found")
    void shouldThrowException_WhenNotFound() {
      when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> bookingService.cancelBooking(99L));
      assertEquals(ErrorCode.BOOKING_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== confirmBooking ====================

  @Nested
  @DisplayName("confirmBooking")
  class ConfirmBooking {

    @Test
    @DisplayName("Should confirm booking successfully")
    void shouldConfirmBooking() {
      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
      when(bookingRepository.save(any())).thenReturn(booking);
      when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

      BookingDto result = bookingService.confirmBooking(1L);

      assertNotNull(result);
      verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Should throw ConflictException when CANCELLED")
    void shouldThrowException_WhenCancelled() {
      booking.setStatus(BookingStatus.CANCELLED);
      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

      ConflictException ex = assertThrows(ConflictException.class,
          () -> bookingService.confirmBooking(1L));
      assertEquals(ErrorCode.CAN_NOT_CONFIRM, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when COMPLETED")
    void shouldThrowException_WhenCompleted() {
      booking.setStatus(BookingStatus.COMPLETED);
      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

      ConflictException ex = assertThrows(ConflictException.class,
          () -> bookingService.confirmBooking(1L));
      assertEquals(ErrorCode.CAN_NOT_CONFIRM, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when already CONFIRMED")
    void shouldThrowException_WhenAlreadyConfirmed() {
      booking.setStatus(BookingStatus.CONFIRMED);
      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

      ConflictException ex = assertThrows(ConflictException.class,
          () -> bookingService.confirmBooking(1L));
      assertEquals(ErrorCode.CAN_NOT_CONFIRM, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw NotFoundException when booking not found")
    void shouldThrowException_WhenNotFound() {
      when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> bookingService.confirmBooking(99L));
      assertEquals(ErrorCode.BOOKING_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== deleteBooking ====================

  @Nested
  @DisplayName("deleteBooking")
  class DeleteBooking {

    @Test
    @DisplayName("Should delete booking successfully")
    void shouldDeleteBooking() {
      when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

      bookingService.deleteBooking(1L);

      verify(bookingCache).clear();
      verify(bookingRepository).delete(booking);
    }

    @Test
    @DisplayName("Should throw NotFoundException when booking not found")
    void shouldThrowException_WhenNotFound() {
      when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> bookingService.deleteBooking(99L));
      assertEquals(ErrorCode.BOOKING_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== getAllBookings ====================

  @Nested
  @DisplayName("getAllBookings")
  class GetAllBookings {

    @Test
    @DisplayName("Should return page of bookings")
    void shouldReturnPage() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<Booking> bookingPage = new PageImpl<>(List.of(booking), pageable, 1);
      when(bookingRepository.findAll(pageable)).thenReturn(bookingPage);
      when(bookingMapper.toDto(any())).thenReturn(bookingDto);

      Page<BookingDto> result = bookingService.getAllBookings(pageable);

      assertNotNull(result);
      assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when no bookings")
    void shouldReturnEmptyPage() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<Booking> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
      when(bookingRepository.findAll(pageable)).thenReturn(emptyPage);

      Page<BookingDto> result = bookingService.getAllBookings(pageable);

      assertNotNull(result);
      assertEquals(0, result.getTotalElements());
    }
  }

  // ==================== createBookingsBulk ====================

  @Nested
  @DisplayName("createBookingsBulk")
  class CreateBookingsBulk {

    @Test
    @DisplayName("Should create multiple bookings")
    void shouldCreateMultipleBookings() {
      List<BookingCreateDto> dtos = List.of(createDto, createDto);
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(bookingRepository.findConflictingBookings(anyLong(), any(), any()))
          .thenReturn(Collections.emptyList());
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(bookingMapper.toEntity(any(), any(), any())).thenReturn(booking);
      when(bookingRepository.save(any())).thenReturn(booking);
      when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

      List<BookingDto> result = bookingService.createBookingsBulk(dtos);

      assertEquals(2, result.size());
      verify(bookingCache, times(2)).clear();
    }

    @Test
    @DisplayName("Should throw exception when list is empty")
    void shouldThrowException_WhenEmptyList() {
      UnprocessableContentException ex = assertThrows(UnprocessableContentException.class,
          () -> bookingService.createBookingsBulk(Collections.emptyList()));
      assertEquals(ErrorCode.BOOKING_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw NullPointerException when list is null")
    void shouldThrowException_WhenNullList() {
      assertThrows(NullPointerException.class,
          () -> bookingService.createBookingsBulk(null));
    }
  }

  // ==================== getUserBookings ====================

  @Nested
  @DisplayName("getUserBookings")
  class GetUserBookings {

    @Test
    @DisplayName("Should return from cache when present")
    void shouldReturnFromCache() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<BookingDto> cachedPage = new PageImpl<>(List.of(bookingDto), pageable, 1);
      when(bookingCache.get(anyLong(), anyLong(), any())).thenReturn(cachedPage);

      Page<BookingDto> result = bookingService.getUserBookings(100L, 2L, pageable);

      assertNotNull(result);
      verify(bookingRepository, never()).findBookingsByUserId(anyLong(), anyLong(), any());
    }

    @Test
    @DisplayName("Should load from DB when cache miss")
    void shouldLoadFromDbOnCacheMiss() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<Booking> bookingPage = new PageImpl<>(List.of(booking), pageable, 1);
      when(bookingCache.get(anyLong(), anyLong(), any())).thenReturn(null);
      when(bookingRepository.findBookingsByUserId(anyLong(), anyLong(), any()))
          .thenReturn(bookingPage);
      when(bookingMapper.toDto(any())).thenReturn(bookingDto);

      Page<BookingDto> result = bookingService.getUserBookings(100L, 2L, pageable);

      assertNotNull(result);
      verify(bookingCache).put(anyLong(), anyLong(), any(), any());
    }

    @Test
    @DisplayName("Should return empty page from cache")
    void shouldReturnEmptyFromCache() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<BookingDto> emptyCached = new PageImpl<>(Collections.emptyList(), pageable, 0);
      when(bookingCache.get(anyLong(), anyLong(), any())).thenReturn(emptyCached);

      Page<BookingDto> result = bookingService.getUserBookings(100L, 2L, pageable);

      assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page from DB")
    void shouldReturnEmptyFromDb() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<Booking> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
      when(bookingCache.get(anyLong(), anyLong(), any())).thenReturn(null);
      when(bookingRepository.findBookingsByUserId(anyLong(), anyLong(), any()))
          .thenReturn(emptyPage);

      Page<BookingDto> result = bookingService.getUserBookings(100L, 2L, pageable);

      assertEquals(0, result.getTotalElements());
      verify(bookingCache).put(anyLong(), anyLong(), any(), any());
    }
  }

  // ==================== getUserBookingsNative ====================

  @Nested
  @DisplayName("getUserBookingsNative")
  class GetUserBookingsNative {

    @Test
    @DisplayName("Should return from cache when present")
    void shouldReturnFromCache() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<BookingDto> cachedPage = new PageImpl<>(List.of(bookingDto), pageable, 1);
      when(bookingCache.get(anyLong(), anyLong(), any())).thenReturn(cachedPage);

      Page<BookingDto> result = bookingService.getUserBookingsNative(100L, 2L, pageable);

      assertNotNull(result);
      verify(bookingRepository, never()).findBookingsByUserIdNative(anyLong(), anyLong(), any());
    }

    @Test
    @DisplayName("Should load from DB when cache miss")
    void shouldLoadFromDbOnCacheMiss() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<Booking> bookingPage = new PageImpl<>(List.of(booking), pageable, 1);
      when(bookingCache.get(anyLong(), anyLong(), any())).thenReturn(null);
      when(bookingRepository.findBookingsByUserIdNative(anyLong(), anyLong(), any()))
          .thenReturn(bookingPage);
      when(bookingMapper.toDto(any())).thenReturn(bookingDto);

      Page<BookingDto> result = bookingService.getUserBookingsNative(100L, 2L, pageable);

      assertNotNull(result);
      verify(bookingCache).put(anyLong(), anyLong(), any(), any());
    }

    @Test
    @DisplayName("Should return empty page from cache")
    void shouldReturnEmptyFromCache() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<BookingDto> emptyCached = new PageImpl<>(Collections.emptyList(), pageable, 0);
      when(bookingCache.get(anyLong(), anyLong(), any())).thenReturn(emptyCached);

      Page<BookingDto> result = bookingService.getUserBookingsNative(100L, 2L, pageable);

      assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page from DB")
    void shouldReturnEmptyFromDb() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<Booking> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
      when(bookingCache.get(anyLong(), anyLong(), any())).thenReturn(null);
      when(bookingRepository.findBookingsByUserIdNative(anyLong(), anyLong(), any()))
          .thenReturn(emptyPage);

      Page<BookingDto> result = bookingService.getUserBookingsNative(100L, 2L, pageable);

      assertEquals(0, result.getTotalElements());
      verify(bookingCache).put(anyLong(), anyLong(), any(), any());
    }
  }

  // ==================== getWorkspaceBookings ====================

  @Nested
  @DisplayName("getWorkspaceBookings")
  class GetWorkspaceBookings {

    @Test
    @DisplayName("Should return bookings for workspace")
    void shouldReturnBookings() {
      when(workspaceRepository.existsById(1L)).thenReturn(true);
      when(bookingRepository.findByWorkspaceId(1L)).thenReturn(List.of(booking));
      when(bookingMapper.toDtoList(any())).thenReturn(List.of(bookingDto));

      List<BookingDto> result = bookingService.getWorkspaceBookings(1L);

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should throw NotFoundException when workspace not found")
    void shouldThrowException_WhenWorkspaceNotFound() {
      when(workspaceRepository.existsById(99L)).thenReturn(false);

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> bookingService.getWorkspaceBookings(99L));
      assertEquals(ErrorCode.WORKSPACE_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== getUserActiveBookings ====================

  @Nested
  @DisplayName("getUserActiveBookings")
  class GetUserActiveBookings {

    @Test
    @DisplayName("Should return active bookings")
    void shouldReturnActiveBookings() {
      when(bookingRepository.findByUserIdAndStatus(1L, BookingStatus.PENDING))
          .thenReturn(List.of(booking));
      when(bookingMapper.toDtoList(any())).thenReturn(List.of(bookingDto));

      List<BookingDto> result = bookingService.getUserActiveBookings(1L);

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no active bookings")
    void shouldReturnEmpty_WhenNoActiveBookings() {
      when(bookingRepository.findByUserIdAndStatus(1L, BookingStatus.PENDING))
          .thenReturn(List.of());
      when(bookingMapper.toDtoList(List.of())).thenReturn(List.of());

      List<BookingDto> result = bookingService.getUserActiveBookings(1L);

      assertTrue(result.isEmpty());
    }
  }
}

