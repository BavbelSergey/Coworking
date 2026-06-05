package com.example.coworking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на создание удобства")
public class AmenityCreateDto {

  @NotBlank(message = "Amenity name is required")
  @Size(min = 2, max = 100, message = "Amenity name must be between 2 and 100 characters")
  @Schema(description = "Название удобства", example = "Wi-Fi", minLength = 2, maxLength = 100)
  private String name;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  @Schema(description = "Описание удобства", example = "Высокоскоростной интернет до 100 Мбит/с", maxLength = 500)
  private String description;
}