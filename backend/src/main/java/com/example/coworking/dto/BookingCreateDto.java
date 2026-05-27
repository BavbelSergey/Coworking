package com.example.coworking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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

  @NotNull(message = "Booking start date is required")
  @FutureOrPresent(message = "Start date must be in the present or future")
  @JsonFormat(pattern = "yyyy-MM-dd")
  @Schema(description = "Дата начала бронирования", example = "2026-05-10")
  private LocalDate startDate;

  @NotNull(message = "Booking end date is required")
  @FutureOrPresent(message = "End date must be in the present or future")
  @JsonFormat(pattern = "yyyy-MM-dd")
  @Schema(description = "Дата окончания бронирования", example = "2026-05-10")
  private LocalDate endDate;

  @NotNull(message = "User ID is required")
  @Schema(description = "ID пользователя", example = "1")
  private Long userId;

  @NotNull(message = "Workspace ID is required")
  @Schema(description = "ID рабочего места", example = "1")
  private Long workspaceId;
}