package com.example.coworking.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmenityUpdateDto {

  @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
  private String name;

  @Size(max = 200, message = "Описание не должно превышать 200 символов")
  private String description;
}