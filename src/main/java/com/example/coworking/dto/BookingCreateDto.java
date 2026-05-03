package com.example.coworking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreateDto {

  @NotNull(message = "Booking start time is required")
  @Future(message = "Start time must be in the future")
  private LocalDateTime startTime;

  @NotNull(message = "Booking end time is required")
  @Future(message = "End time must be in the future")
  private LocalDateTime endTime;

  @NotNull(message = "User ID is required")
  private Long userId;

  @NotNull(message = "Workspace ID is required")
  private Long workspaceId;
}