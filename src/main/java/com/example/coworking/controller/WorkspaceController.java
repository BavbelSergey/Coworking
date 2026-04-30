package com.example.coworking.controller;

import com.example.coworking.cache.BookingSearchCache;
import com.example.coworking.dto.WorkspaceDto;
import com.example.coworking.service.WorkspaceService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

  private final WorkspaceService workspaceService;
  private final BookingSearchCache searchCache;

  @GetMapping
  public ResponseEntity<Page<WorkspaceDto>> getAllWorkspaces(
      @PageableDefault(size = 20, sort = "number") Pageable pageable) {
    return ResponseEntity.ok(workspaceService.getAllWorkspaces(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<WorkspaceDto> getWorkspaceById(@PathVariable Long id) {
    WorkspaceDto workspace = workspaceService.getWorkspaceById(id);
    return ResponseEntity.ok(workspace);
  }

  @PostMapping
  public ResponseEntity<WorkspaceDto> createWorkspace(
      @Valid @RequestBody WorkspaceDto workspaceDto) {
    WorkspaceDto createdWorkspace = workspaceService.createWorkspace(workspaceDto);
    searchCache.clear();
    return new ResponseEntity<>(createdWorkspace, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<WorkspaceDto> updateWorkspace(@PathVariable Long id,
      @Valid @RequestBody WorkspaceDto workspaceDto) {
    WorkspaceDto updatedWorkspace = workspaceService.updateWorkspace(id, workspaceDto);
    searchCache.clear();
    return ResponseEntity.ok(updatedWorkspace);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<WorkspaceDto> partialUpdateWorkspace(@PathVariable Long id,
      @RequestBody WorkspaceDto workspaceDto) {
    WorkspaceDto updatedWorkspace = workspaceService.partialUpdateWorkspace(id, workspaceDto);
    searchCache.clear();
    return ResponseEntity.ok(updatedWorkspace);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWorkspace(@PathVariable Long id) {
    workspaceService.deleteWorkspace(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/available")
  public ResponseEntity<List<WorkspaceDto>> getAvailableWorkspaces(
      @RequestParam(required = false) Integer minCapacity,
      @RequestParam(required = false) Double maxPrice,
      @RequestParam(required = false) List<Long> amenityIds) {
    List<WorkspaceDto> availableWorkspaces = workspaceService.findAvailableWorkspaces(minCapacity,
        maxPrice, amenityIds);
    return ResponseEntity.ok(availableWorkspaces);
  }

  @GetMapping("/capacity/{minCapacity}")
  public ResponseEntity<List<WorkspaceDto>> getWorkspacesByMinCapacity(
      @PathVariable Integer minCapacity) {
    List<WorkspaceDto> workspaces = workspaceService.getWorkspacesByMinCapacity(minCapacity);
    return ResponseEntity.ok(workspaces);
  }

  @GetMapping("/price")
  public ResponseEntity<List<WorkspaceDto>> getWorkspacesByMaxPrice(@RequestParam Double maxPrice) {
    List<WorkspaceDto> workspaces = workspaceService.getWorkspacesByMaxPrice(maxPrice);
    return ResponseEntity.ok(workspaces);
  }

  @PostMapping("/{workspaceId}/amenities/{amenityId}")
  public ResponseEntity<WorkspaceDto> addAmenityToWorkspace(@PathVariable Long workspaceId,
      @PathVariable Long amenityId) {
    WorkspaceDto updatedWorkspace = workspaceService.addAmenityToWorkspace(workspaceId, amenityId);
    searchCache.clear();
    return ResponseEntity.ok(updatedWorkspace);
  }

  @DeleteMapping("/{workspaceId}/amenities/{amenityId}")
  public ResponseEntity<WorkspaceDto> removeAmenityFromWorkspace(@PathVariable Long workspaceId,
      @PathVariable Long amenityId) {
    WorkspaceDto updatedWorkspace = workspaceService.removeAmenityFromWorkspace(workspaceId,
        amenityId);
    return ResponseEntity.ok(updatedWorkspace);
  }

  @GetMapping("/exists/number/{number}")
  public ResponseEntity<Boolean> existsByNumber(@PathVariable Integer number) {
    boolean exists = workspaceService.existsByNumber(number);
    return ResponseEntity.ok(exists);
  }

  @DeleteMapping("/number/{number}")
  public ResponseEntity<Void> deleteByNumber(@PathVariable Integer number) {
    workspaceService.deleteByNumber(number);
    return ResponseEntity.noContent().build();
  }
}