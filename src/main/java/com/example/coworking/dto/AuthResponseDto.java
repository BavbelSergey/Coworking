package com.example.coworking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Ответ аутентификации")
public class AuthResponseDto {

  @Schema(description = "JWT access token")
  private String token;

  @Schema(description = "Тип токена", example = "Bearer")
  private String tokenType;

  @Schema(description = "Срок действия токена в миллисекундах", example = "86400000")
  private long expiresIn;
}
