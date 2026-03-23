package com.example.coworking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDto {

  @NotBlank(message = "Имя обязательно")
  private String name;

  @NotBlank(message = "Email обязателен")
  @Email(message = "Некорректный формат email")
  private String email;

  @Pattern(regexp = "^\\+?[0-9\\s-]{10,15}$", message = "Некорректный формат телефона")
  private String phone;
}