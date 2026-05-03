package com.example.coworking.controller;

import com.example.coworking.dto.AmenityCreateDto;
import com.example.coworking.dto.AmenityDto;
import com.example.coworking.dto.AmenityUpdateDto;
import com.example.coworking.service.AmenityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
@Validated
@Tag(name = "Amenities", description = "Управление удобствами коворкинга")
public class AmenityController {

  private final AmenityService amenityService;

  @Operation(
      summary = "Получить список удобств",
      description = "Возвращает список удобств с пагинацией и сортировкой"
  )
  @GetMapping
  public Page<AmenityDto> getAllAmenities(
      @Parameter(hidden = true) Pageable pageable
  ) {
    return amenityService.getAllAmenities(pageable);
  }

  @Operation(
      summary = "Получить удобство по ID",
      description = "Возвращает детальную информацию об удобстве"
  )
  @GetMapping("/{id}")
  public AmenityDto getAmenityById(
      @Parameter(description = "ID удобства", example = "1")
      @PathVariable @Positive(message = "ID must be a positive number") Long id
  ) {
    return amenityService.getAmenityById(id);
  }

  @Operation(
      summary = "Получить удобство по названию",
      description = "Возвращает удобство по его точному названию"
  )
  @GetMapping("/name/{name}")
  public AmenityDto getAmenityByName(
      @Parameter(description = "Название удобства", example = "Wi-Fi")
      @PathVariable @NotBlank(message = "Name cannot be blank") String name
  ) {
    return amenityService.getAmenityByName(name);
  }

  @Operation(
      summary = "Создать удобство",
      description = "Создаёт новое удобство с уникальным названием"
  )
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AmenityDto createAmenity(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Данные для создания удобства",
          required = true
      )
      @Valid @RequestBody AmenityCreateDto createDto
  ) {
    return amenityService.createAmenity(createDto);
  }

  @Operation(
      summary = "Обновить удобство",
      description = "Полностью обновляет все поля удобства по ID"
  )
  @PutMapping("/{id}")
  public AmenityDto updateAmenity(
      @Parameter(description = "ID удобства", example = "1")
      @PathVariable @Positive(message = "ID must be a positive number") Long id,

      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Новые данные удобства",
          required = true
      )
      @Valid @RequestBody AmenityUpdateDto updateDto
  ) {
    return amenityService.updateAmenity(id, updateDto);
  }

  @Operation(
      summary = "Удалить удобство по ID",
      description = "Удаляет удобство, если оно не используется в рабочих местах"
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAmenity(
      @Parameter(description = "ID удобства", example = "1")
      @PathVariable @Positive(message = "ID must be a positive number") Long id
  ) {
    amenityService.deleteAmenity(id);
  }

  @Operation(
      summary = "Удалить удобство по названию",
      description = "Удаляет удобство по точному названию, если оно не используется"
  )
  @DeleteMapping("/name/{name}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAmenityByName(
      @Parameter(description = "Название удобства", example = "Wi-Fi")
      @PathVariable @NotBlank(message = "Name cannot be blank") String name
  ) {
    amenityService.deleteAmenityByName(name);
  }

  @Operation(
      summary = "Поиск удобств по названию",
      description = "Возвращает удобства, "
          + "название которых содержит указанную строку (без учёта регистра)"
  )
  @GetMapping("/search/name")
  public List<AmenityDto> searchAmenitiesByName(
      @Parameter(description = "Строка для поиска в названии", example = "Wi")
      @RequestParam @NotBlank(message = "Search name cannot be blank") String name
  ) {
    return amenityService.searchAmenitiesByName(name);
  }

  @Operation(
      summary = "Поиск удобств по описанию",
      description = "Возвращает удобства, "
          + "описание которых содержит указанную строку (без учёта регистра)"
  )
  @GetMapping("/search/description")
  public List<AmenityDto> searchAmenitiesByDescription(
      @Parameter(description = "Строка для поиска в описании", example = "скоростной")
      @RequestParam @NotBlank(message = "Search description cannot be blank") String description
  ) {
    return amenityService.searchAmenitiesByDescription(description);
  }

  @Operation(
      summary = "Удобства по минимальной вместимости",
      description = "Возвращает удобства рабочих мест с вместимостью не менее указанной"
  )
  @GetMapping("/by-capacity/{minCapacity}")
  public List<AmenityDto> getAmenitiesByMinCapacity(
      @Parameter(description = "Минимальная вместимость", example = "4")
      @PathVariable @Min(value = 0, message =
          "Minimum capacity cannot be negative") Integer minCapacity
  ) {
    return amenityService.getAmenitiesByMinCapacity(minCapacity);
  }

  @Operation(
      summary = "Удобства по максимальной цене",
      description = "Возвращает удобства рабочих мест с ценой не выше указанной"
  )
  @GetMapping("/by-price")
  public List<AmenityDto> getAmenitiesByMaxPrice(
      @Parameter(description = "Максимальная цена в час", example = "500.00")
      @RequestParam @Positive(message = "Maximum price must be positive") Double maxPrice
  ) {
    return amenityService.getAmenitiesByMaxPrice(maxPrice);
  }

  @Operation(
      summary = "Получить все названия удобств",
      description = "Возвращает список названий всех удобств"
  )
  @GetMapping("/names")
  public List<String> getAllAmenityNames() {
    return amenityService.getAllAmenityNames();
  }

  @Operation(
      summary = "Удобства не привязанные к рабочему месту",
      description = "Возвращает удобства, которые не назначены указанному рабочему месту"
  )
  @GetMapping("/not-in-workspace/{workspaceId}")
  public List<AmenityDto> getAmenitiesNotInWorkspace(
      @Parameter(description = "ID рабочего места", example = "1")
      @PathVariable @Positive(message = "Workspace ID must be a positive number") Long workspaceId
  ) {
    return amenityService.getAmenitiesNotInWorkspace(workspaceId);
  }

  @Operation(
      summary = "Статистика по удобствам",
      description = "Возвращает количество рабочих мест для каждого удобства"
  )
  @GetMapping("/statistics")
  public List<Object[]> getAmenityStatistics() {
    return amenityService.getAmenityStatistics();
  }
}