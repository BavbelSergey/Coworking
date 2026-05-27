package com.example.coworking.dto;

import com.example.coworking.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Пользователь")
public class UserDto {

  @Schema(description = "ID пользователя", example = "1")
  private Long id;

  @Schema(description = "Имя пользователя", example = "Иван Петров")
  private String name;

  @Schema(description = "Email пользователя", example = "ivan@example.com")
  private String email;

  @Schema(description = "Номер телефона", example = "+79161234567")
  private String phone;

  @Schema(description = "Роль пользователя", example = "USER")
  private UserRole role;
}
