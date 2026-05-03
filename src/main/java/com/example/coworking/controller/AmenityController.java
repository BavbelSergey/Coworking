package com.example.coworking.controller;

import com.example.coworking.dto.AmenityCreateDto;
import com.example.coworking.dto.AmenityDto;
import com.example.coworking.dto.AmenityUpdateDto;
import com.example.coworking.service.AmenityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
@Validated
public class AmenityController {

  private final AmenityService amenityService;

  @GetMapping
  public ResponseEntity<Page<AmenityDto>> getAllAmenities(
      @PageableDefault(size = 20, sort = "id") Pageable pageable) {
    return ResponseEntity.ok(amenityService.getAllAmenities(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AmenityDto> getAmenityById(
      @PathVariable @Positive(message = "ID must be a positive number") Long id) {
    return ResponseEntity.ok(amenityService.getAmenityById(id));
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<AmenityDto> getAmenityByName(
      @PathVariable @NotBlank(message = "Name cannot be blank") String name) {
    return ResponseEntity.ok(amenityService.getAmenityByName(name));
  }

  @PostMapping
  public ResponseEntity<AmenityDto> createAmenity(@Valid @RequestBody AmenityCreateDto createDto) {
    AmenityDto createdAmenity = amenityService.createAmenity(createDto);
    return new ResponseEntity<>(createdAmenity, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AmenityDto> updateAmenity(
      @PathVariable @Positive(message = "ID must be a positive number") Long id,
      @Valid @RequestBody AmenityUpdateDto updateDto) {
    AmenityDto updatedAmenity = amenityService.updateAmenity(id, updateDto);
    return ResponseEntity.ok(updatedAmenity);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<AmenityDto> partialUpdateAmenity(
      @PathVariable @Positive(message = "ID must be a positive number") Long id,
      @Valid @RequestBody AmenityUpdateDto updateDto) {
    AmenityDto updatedAmenity = amenityService.partialUpdateAmenity(id, updateDto);
    return ResponseEntity.ok(updatedAmenity);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAmenity(
      @PathVariable @Positive(message = "ID must be a positive number") Long id) {
    amenityService.deleteAmenity(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/name/{name}")
  public ResponseEntity<Void> deleteAmenityByName(
      @PathVariable @NotBlank(message = "Name cannot be blank") String name) {
    amenityService.deleteAmenityByName(name);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search/name")
  public ResponseEntity<List<AmenityDto>> searchAmenitiesByName(
      @RequestParam @NotBlank(message = "Search name cannot be blank") String name) {
    return ResponseEntity.ok(amenityService.searchAmenitiesByName(name));
  }

  @GetMapping("/search/description")
  public ResponseEntity<List<AmenityDto>> searchAmenitiesByDescription(
      @RequestParam @NotBlank(message = "Search description cannot be blank") String description) {
    return ResponseEntity.ok(amenityService.searchAmenitiesByDescription(description));
  }

  @GetMapping("/by-capacity/{minCapacity}")
  public ResponseEntity<List<AmenityDto>> getAmenitiesByMinCapacity(
      @PathVariable @Min(value = 0, message =
          "Minimum capacity cannot be negative") Integer minCapacity) {
    return ResponseEntity.ok(amenityService.getAmenitiesByMinCapacity(minCapacity));
  }

  @GetMapping("/by-price")
  public ResponseEntity<List<AmenityDto>> getAmenitiesByMaxPrice(
      @RequestParam @Positive(message =
          "Maximum price must be positive") Double maxPrice) {
    return ResponseEntity.ok(amenityService.getAmenitiesByMaxPrice(maxPrice));
  }

  @GetMapping("/names")
  public ResponseEntity<List<String>> getAllAmenityNames() {
    return ResponseEntity.ok(amenityService.getAllAmenityNames());
  }

  @GetMapping("/not-in-workspace/{workspaceId}")
  public ResponseEntity<List<AmenityDto>> getAmenitiesNotInWorkspace(
      @PathVariable @Positive(message =
          "Workspace ID must be a positive number") Long workspaceId) {
    return ResponseEntity.ok(amenityService.getAmenitiesNotInWorkspace(workspaceId));
  }

  @GetMapping("/statistics")
  public ResponseEntity<List<Object[]>> getAmenityStatistics() {
    return ResponseEntity.ok(amenityService.getAmenityStatistics());
  }
}