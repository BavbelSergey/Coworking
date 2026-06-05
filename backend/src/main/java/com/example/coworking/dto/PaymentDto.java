package com.example.coworking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@Schema(description = "Платёж")
public class PaymentDto {

  @Schema(description = "ID платежа", example = "1")
  private Long id;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(description = "Сумма платежа", example = "1500.00")
  private BigDecimal amount;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "Дата платежа", example = "2026-05-03 21:30:00")
  private LocalDateTime date;

  @Schema(description = "Способ оплаты", example = "CARD", allowableValues = {"CARD", "CASH",
      "TRANSFER"})
  private String paymentMethod;

  @Schema(description = "ID бронирования", example = "1")
  private Long bookingId;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @Schema(description = "Дата начала бронирования", example = "2026-05-10")
  private LocalDate bookingStartDate;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @Schema(description = "Дата окончания бронирования", example = "2026-05-10")
  private LocalDate bookingEndDate;

  @Schema(description = "ID пользователя", example = "1")
  private Long userId;

  @Schema(description = "Имя пользователя", example = "Иван Петров")
  private String userName;

  @Schema(description = "ID рабочего места", example = "1")
  private Long workspaceId;
}