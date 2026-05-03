package com.example.coworking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на создание платежа")
public class PaymentCreateDto {

  @NotNull(message = "Сумма обязательна")
  @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
  @Schema(description = "Сумма платежа", example = "1500.00")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private BigDecimal amount;

  @NotNull(message = "Способ оплаты обязателен")
  @Pattern(regexp = "CARD|CASH|TRANSFER", message = "Способ оплаты CARD, CASH или TRANSFER")
  @Schema(description = "Способ оплаты", example = "CARD", allowableValues = {"CARD", "CASH", "TRANSFER"})
  private String paymentMethod;

  @NotNull(message = "ID брони обязателен")
  @Schema(description = "ID бронирования", example = "1")
  private Long bookingId;
}