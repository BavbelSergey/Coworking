package com.example.coworking.dto;

import com.example.coworking.model.BookingStatus;
import jakarta.validation.constraints.Future;
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

  @Future(message = "Start time must be in the future")
  private LocalDateTime startTime;

  @Future(message = "End time must be in the future")
  private LocalDateTime endTime;
  private BookingStatus status;
}