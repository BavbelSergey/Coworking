package com.example.coworking.mapper;

import com.example.coworking.dto.BookingCreateDto;
import com.example.coworking.dto.BookingDto;
import com.example.coworking.dto.BookingUpdateDto;
import com.example.coworking.model.Booking;
import com.example.coworking.model.BookingStatus;
import com.example.coworking.model.User;
import com.example.coworking.model.Workspace;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

  public BookingDto toDto(Booking booking) {
    if (booking == null) {
      return null;
    }

    BookingDto dto = new BookingDto();
    dto.setId(booking.getId());
    dto.setStartDate(booking.getStartDate());
    dto.setEndDate(booking.getEndDate());
    dto.setCreatedAt(booking.getCreatedAt());
    dto.setStatus(booking.getStatus());

    if (booking.getUser() != null) {
      dto.setUserId(booking.getUser().getId());
      dto.setUserName(booking.getUser().getName());
      dto.setUserEmail(booking.getUser().getEmail());
    }

    if (booking.getWorkspace() != null) {
      dto.setWorkspaceId(booking.getWorkspace().getId());
      dto.setWorkspaceName(booking.getWorkspace().getName());
    }

    if (booking.getPayment() != null) {
      dto.setPaymentId(booking.getPayment().getId());
    }

    return dto;
  }

  public Booking toEntity(BookingCreateDto createDto, User user, Workspace workspace) {
    if (createDto == null) {
      return null;
    }

    Booking booking = new Booking();
    booking.setStartDate(createDto.getStartDate());
    booking.setEndDate(createDto.getEndDate());
    booking.setCreatedAt(LocalDateTime.now());
    booking.setStatus(BookingStatus.PENDING);
    booking.setUser(user);
    booking.setWorkspace(workspace);

    return booking;
  }

  public void updateEntity(BookingUpdateDto updateDto, Booking booking) {
    if (updateDto == null || booking == null) {
      return;
    }

    if (updateDto.getStartDate() != null) {
      booking.setStartDate(updateDto.getStartDate());
    }
    if (updateDto.getEndDate() != null) {
      booking.setEndDate(updateDto.getEndDate());
    }
    if (updateDto.getStatus() != null) {
      booking.setStatus(updateDto.getStatus());
    }
  }

  public List<BookingDto> toDtoList(List<Booking> bookings) {
    if (bookings == null) {
      return new ArrayList<>();
    }
    return bookings.stream().map(this::toDto).toList();
  }
}