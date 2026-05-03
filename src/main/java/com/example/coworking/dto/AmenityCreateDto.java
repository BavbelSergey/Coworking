package com.example.coworking.dto;

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
public class AmenityCreateDto {

  @NotBlank(message = "Amenity name is required")
  @Size(min = 2, max = 100, message = "Amenity name must be between 2 and 100 characters")
  private String name;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;
}