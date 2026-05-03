package com.example.coworking.dto;

import com.example.coworking.model.BookingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Запрос на обновление бронирования")
public class BookingUpdateDto {

  @Future(message = "Start time must be in the future")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "Новое время начала бронирования", example = "2026-05-10 10:00:00")
  private LocalDateTime startTime;

  @Future(message = "End time must be in the future")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "Новое время окончания бронирования", example = "2026-05-10 19:00:00")
  private LocalDateTime endTime;

  @Schema(description = "Новый статус бронирования", example = "CONFIRMED")
  private BookingStatus status;
}