package com.example.coworking.dto;

import com.example.coworking.model.BookingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Бронирование")
public class BookingDto {

  @Schema(description = "ID бронирования", example = "1")
  private Long id;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "Время начала бронирования", example = "2026-05-10 09:00:00")
  private LocalDateTime startTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "Время окончания бронирования", example = "2026-05-10 18:00:00")
  private LocalDateTime endTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "Дата создания бронирования", example = "2026-05-03 21:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "Статус бронирования", example = "PENDING")
  private BookingStatus status;

  @Schema(description = "ID пользователя", example = "1")
  private Long userId;

  @Schema(description = "ID рабочего места", example = "1")
  private Long workspaceId;

  @Schema(description = "ID платежа", example = "1")
  private Long paymentId;

  @Schema(description = "Имя пользователя", example = "Иван Петров")
  private String userName;

  @Schema(description = "Email пользователя", example = "ivan@example.com")
  private String userEmail;

  @Schema(description = "Номер рабочего места", example = "101")
  private Integer workspaceNumber;
}