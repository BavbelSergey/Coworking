package com.example.coworking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на создание пользователя")
public class UserCreateDto {

  @NotBlank(message = "Имя обязательно")
  @Schema(description = "Имя пользователя", example = "Иван Петров")
  private String name;

  @NotBlank(message = "Email обязателен")
  @Email(message = "Некорректный формат email")
  @Schema(description = "Email пользователя", example = "ivan@example.com")
  private String email;

  @Pattern(regexp = "^\\+?[0-9\\s-]{10,15}$", message = "Некорректный формат телефона")
  @Schema(description = "Номер телефона", example = "+79161234567")
  private String phone;

  @NotBlank(message = "Пароль обязателен")
  @Size(min = 8, message = "Пароль должен быть не короче 8 символов")
  @Schema(description = "Пароль пользователя", example = "strongPassword123")
  private String password;
}
