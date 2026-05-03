package com.example.coworking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на обновление платежа")
public class PaymentUpdateDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(description = "Новая сумма платежа", example = "2000.00")
  private BigDecimal amount;

  @Schema(description = "Новый способ оплаты", example = "TRANSFER", allowableValues = {"CARD", "CASH", "TRANSFER"})
  private String paymentMethod;
}