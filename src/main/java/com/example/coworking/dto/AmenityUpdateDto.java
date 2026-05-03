package com.example.coworking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на обновление удобства")
public class AmenityUpdateDto {

  @Size(min = 2, max = 100, message = "Amenity name must be between 2 and 100 characters")
  @Schema(description = "Новое название удобства", example = "Wi-Fi Premium", minLength = 2, maxLength = 100)
  private String name;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  @Schema(description = "Новое описание удобства", example =
      "Высокоскоростной интернет до 300 Мбит/с", maxLength = 500)
  private String description;
}