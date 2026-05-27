package com.example.coworking.dto;

import com.example.coworking.model.BookingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import java.time.LocalDate;
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

  @Future(message = "Start date must be in the future")
  @JsonFormat(pattern = "yyyy-MM-dd")
  @Schema(description = "Новая дата начала бронирования", example = "2026-05-10")
  private LocalDate startDate;

  @Future(message = "End date must be in the future")
  @JsonFormat(pattern = "yyyy-MM-dd")
  @Schema(description = "Новая дата окончания бронирования", example = "2026-05-10")
  private LocalDate endDate;

  @Schema(description = "Новый статус бронирования", example = "CONFIRMED")
  private BookingStatus status;
}