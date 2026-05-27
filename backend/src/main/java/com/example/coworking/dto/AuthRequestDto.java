package com.example.coworking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на вход")
public class AuthRequestDto {

  @NotBlank(message = "Email обязателен")
  @Email(message = "Некорректный формат email")
  @Schema(description = "Email пользователя", example = "ivan@example.com")
  private String email;

  @NotBlank(message = "Пароль обязателен")
  @Schema(description = "Пароль пользователя", example = "strongPassword123")
  private String password;
}
