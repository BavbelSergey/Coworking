package com.example.coworking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Рабочее место")
public class WorkspaceDto {

  @Schema(description = "ID рабочего места", example = "1")
  private Long id;

  @Schema(description = "Номер рабочего места", example = "101")
  private Integer number;

  @Schema(description = "Вместимость (количество человек)", example = "4")
  private Integer capacity;

  @Schema(description = "Цена за час", example = "500.00")
  private BigDecimal pricePerHour;

  @Schema(description = "Список удобств, привязанных к рабочему месту")
  private List<AmenityDto> amenities;
}