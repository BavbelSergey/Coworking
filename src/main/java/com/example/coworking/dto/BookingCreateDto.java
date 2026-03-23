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

  @NotNull(message = "Время начала обязательно")
  @Future(message = "Время начала должно быть в будущем")
  private LocalDateTime startTime;

  @NotNull(message = "Время окончания обязательно")
  @Future(message = "Время окончания должно быть в будущем")
  private LocalDateTime endTime;

  @NotNull(message = "ID пользователя обязателен")
  private Long userId;

  @NotNull(message = "ID рабочего места обязательно")
  private Long workspaceId;
}