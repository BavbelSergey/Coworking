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
public class AmenityDto {

  @Schema(description = "ID удобства", example = "1")
  private Long id;

  @Schema(description = "Название удобства", example = "Wi-Fi")
  private String name;

  @Schema(description = "Описание удобства", example = "Высокоскоростной интернет до 100 Мбит/с")
  private String description;
}