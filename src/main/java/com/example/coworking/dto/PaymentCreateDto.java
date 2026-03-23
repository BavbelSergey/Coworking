package com.example.coworking.dto;

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
public class PaymentCreateDto {

  @NotNull(message = "Сумма обязательна")
  @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
  private BigDecimal amount;

  @NotNull(message = "Способ оплаты обязателен")
  @Pattern(regexp = "CARD|CASH|TRANSFER", message = "Способ оплаты CARD,CASH или TRANSFER")
  private String paymentMethod;

  @NotNull(message = "ID брони обязателен")
  private Long bookingId;
}