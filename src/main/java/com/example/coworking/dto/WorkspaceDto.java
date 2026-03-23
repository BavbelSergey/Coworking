package com.example.coworking.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkspaceDto {

  private Long id;
  private Integer number;
  private Integer capacity;
  private BigDecimal pricePerHour;
  private List<AmenityDto> amenities;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AmenityDto {

    private Long id;
    private String name;
  }
}
