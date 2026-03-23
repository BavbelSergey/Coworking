package com.example.coworking.dto;

import com.example.coworking.model.BookingStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingUpdateDto {

  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private BookingStatus status;
}