package com.example.coworking.dto;

import com.example.coworking.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на обновление пользователя")
public class UserUpdateDto {

  @Schema(description = "Новое имя пользователя", example = "Иван Сидоров")
  private String name;

  @Email(message = "Некорректный формат email")
  @Schema(description = "Новый email пользователя", example = "ivan_new@example.com")
  private String email;

  @Schema(description = "Новый номер телефона", example = "+79169876543")
  private String phone;

  @Schema(description = "Новая роль пользователя", example = "USER")
  private UserRole role;
}
