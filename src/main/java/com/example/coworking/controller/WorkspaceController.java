package com.example.coworking.controller;

import com.example.coworking.cache.BookingSearchCache;
import com.example.coworking.dto.WorkspaceDto;
import com.example.coworking.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
@Validated
@Tag(name = "Workspaces", description = "Управление рабочими местами коворкинга")
public class WorkspaceController {

  private final WorkspaceService workspaceService;
  private final BookingSearchCache searchCache;

  @Operation(
      summary = "Получить список рабочих мест",
      description = "Возвращает все рабочие места с пагинацией и сортировкой по названию"
  )
  @GetMapping
  public Page<WorkspaceDto> getAllWorkspaces(
      @Parameter(hidden = true) Pageable pageable
  ) {
    return workspaceService.getAllWorkspaces(pageable);
  }

  @Operation(
      summary = "Получить рабочее место по ID",
      description = "Возвращает детальную информацию о рабочем месте, включая amenities"
  )
  @GetMapping("/{id}")
  public WorkspaceDto getWorkspaceById(
      @Parameter(description = "ID рабочего места", example = "1")
      @PathVariable @Positive(message = "Workspace ID must be a positive number") Long id
  ) {
    return workspaceService.getWorkspaceById(id);
  }

  @Operation(
      summary = "Создать рабочее место",
      description = "Создаёт новое рабочее место. "
          + "Название должно быть уникальным. Кеш поиска сбрасывается"
  )
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public WorkspaceDto createWorkspace(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Данные для создания рабочего места",
          required = true
      )
      @Valid @RequestBody WorkspaceDto workspaceDto
  ) {
    WorkspaceDto createdWorkspace = workspaceService.createWorkspace(workspaceDto);
    searchCache.clear();
    return createdWorkspace;
  }

  @Operation(
      summary = "Полностью обновить рабочее место",
      description = "Обновляет все поля рабочего места. "
          + "Название должно быть уникальным. Кеш поиска сбрасывается"
  )
  @PutMapping("/{id}")
  public WorkspaceDto updateWorkspace(
      @Parameter(description = "ID рабочего места", example = "1")
      @PathVariable @Positive(message = "Workspace ID must be a positive number") Long id,

      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Новые данные рабочего места",
          required = true
      )
      @Valid @RequestBody WorkspaceDto workspaceDto
  ) {
    WorkspaceDto updatedWorkspace = workspaceService.updateWorkspace(id, workspaceDto);
    searchCache.clear();
    return updatedWorkspace;
  }

  @Operation(
      summary = "Частично обновить рабочее место",
      description = "Обновляет только переданные поля. "
          + "Можно передать только название, или только цену, и т.д. "
          + "Кеш поиска сбрасывается"
  )
  @PatchMapping("/{id}")
  public WorkspaceDto partialUpdateWorkspace(
      @Parameter(description = "ID рабочего места", example = "1")
      @PathVariable @Positive(message = "Workspace ID must be a positive number") Long id,

      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Поля для частичного обновления (можно передать не все)",
          required = true
      )
      @Valid @RequestBody WorkspaceDto workspaceDto
  ) {
    WorkspaceDto updatedWorkspace = workspaceService.partialUpdateWorkspace(id, workspaceDto);
    searchCache.clear();
    return updatedWorkspace;
  }

  @Operation(
      summary = "Удалить рабочее место",
      description = "Удаляет рабочее место. "
          + "Нельзя удалить, если есть активные (CONFIRMED) бронирования"
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteWorkspace(
      @Parameter(description = "ID рабочего места", example = "1")
      @PathVariable @Positive(message = "Workspace ID must be a positive number") Long id
  ) {
    workspaceService.deleteWorkspace(id);
  }

  @Operation(
      summary = "Найти доступные рабочие места",
      description = "Возвращает рабочие места по фильтрам: минимальная вместимость, "
          + "максимальная цена и список amenities.Все параметры опциональны"
  )
  @GetMapping("/available")
  public List<WorkspaceDto> getAvailableWorkspaces(
      @Parameter(description = "Минимальная вместимость", example = "2")
      @RequestParam(required = false) @Min(value = 0,
          message = "Minimum capacity cannot be negative")
      Integer minCapacity,

      @Parameter(description = "Максимальная цена в час", example = "500.00")
      @RequestParam(required = false) @Positive(message = "Maximum price must be positive")
      Double maxPrice,

      @Parameter(description = "Список ID удобств, "
          + "которые должны быть у рабочего места", example = "[1, 2, 3]")
      @RequestParam(required = false) List<@Positive(message =
          "Amenity ID must be positive") Long> amenityIds
  ) {
    return workspaceService.findAvailableWorkspaces(minCapacity, maxPrice, amenityIds);
  }

  @Operation(
      summary = "Рабочие места по минимальной вместимости",
      description = "Возвращает рабочие места с вместимостью не менее указанной"
  )
  @GetMapping("/capacity/{minCapacity}")
  public List<WorkspaceDto> getWorkspacesByMinCapacity(
      @Parameter(description = "Минимальная вместимость", example = "4")
      @PathVariable @Min(value = 0, message = "Minimum capacity cannot be negative")
      Integer minCapacity
  ) {
    return workspaceService.getWorkspacesByMinCapacity(minCapacity);
  }

  @Operation(
      summary = "Рабочие места по максимальной цене",
      description = "Возвращает рабочие места с ценой не выше указанной"
  )
  @GetMapping("/price")
  public List<WorkspaceDto> getWorkspacesByMaxPrice(
      @Parameter(description = "Максимальная цена в час", example = "500.00")
      @RequestParam @Positive(message = "Maximum price must be positive") Double maxPrice
  ) {
    return workspaceService.getWorkspacesByMaxPrice(maxPrice);
  }

  @Operation(
      summary = "Добавить удобство к рабочему месту",
      description = "Привязывает удобство к рабочему месту. "
          + "Если удобство уже привязано — ничего не делает. "
          + "Кеш поиска сбрасывается"
  )
  @PostMapping("/{workspaceId}/amenities/{amenityId}")
  public WorkspaceDto addAmenityToWorkspace(
      @Parameter(description = "ID рабочего места", example = "1")
      @PathVariable @Positive(message = "Workspace ID must be positive") Long workspaceId,

      @Parameter(description = "ID удобства", example = "1")
      @PathVariable @Positive(message = "Amenity ID must be positive") Long amenityId
  ) {
    WorkspaceDto updatedWorkspace = workspaceService.addAmenityToWorkspace(workspaceId, amenityId);
    searchCache.clear();
    return updatedWorkspace;
  }

  @Operation(
      summary = "Удалить удобство у рабочего места",
      description = "Отвязывает удобство от рабочего места"
  )
  @DeleteMapping("/{workspaceId}/amenities/{amenityId}")
  public WorkspaceDto removeAmenityFromWorkspace(
      @Parameter(description = "ID рабочего места", example = "1")
      @PathVariable @Positive(message = "Workspace ID must be positive") Long workspaceId,

      @Parameter(description = "ID удобства", example = "1")
      @PathVariable @Positive(message = "Amenity ID must be positive") Long amenityId
  ) {
    return workspaceService.removeAmenityFromWorkspace(workspaceId, amenityId);
  }

  @Operation(
      summary = "Проверить существование названия",
      description = "Возвращает true, если рабочее место с таким названием уже существует"
  )
  @GetMapping("/exists/name/{name}")
  public Boolean existsByName(
      @Parameter(description = "Название рабочего места", example = "Meeting Room 101")
      @PathVariable String name
  ) {
    return workspaceService.existsByName(name);
  }

  @Operation(
      summary = "Удалить рабочее место по названию",
      description = "Удаляет рабочее место по его названию. "
          + "Нельзя удалить, если есть активные бронирования"
  )
  @DeleteMapping("/name/{name}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteByName(
      @Parameter(description = "Название рабочего места", example = "Meeting Room 101")
      @PathVariable String name
  ) {
    workspaceService.deleteByName(name);
  }
}
