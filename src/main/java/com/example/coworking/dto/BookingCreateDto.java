package com.example.coworking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Запрос на создание бронирования")
public class BookingCreateDto {

  @NotNull(message = "Booking start time is required")
  @Future(message = "Start time must be in the future")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "Время начала бронирования", example = "2026-05-10 09:00:00")
  private LocalDateTime startTime;

  @NotNull(message = "Booking end time is required")
  @Future(message = "End time must be in the future")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "Время окончания бронирования", example = "2026-05-10 18:00:00")
  private LocalDateTime endTime;

  @NotNull(message = "User ID is required")
  @Schema(description = "ID пользователя", example = "1")
  private Long userId;

  @NotNull(message = "Workspace ID is required")
  @Schema(description = "ID рабочего места", example = "1")
  private Long workspaceId;
}