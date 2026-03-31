package com.example.coworking.service;

import com.example.coworking.dto.WorkspaceDto;
import com.example.coworking.mapper.WorkspaceMapper;
import com.example.coworking.model.Amenity;
import com.example.coworking.model.BookingStatus;
import com.example.coworking.model.Workspace;
import com.example.coworking.repository.AmenityRepository;
import com.example.coworking.repository.WorkspaceRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceService {

  private final WorkspaceRepository workspaceRepository;
  private final AmenityRepository amenityRepository;
  private final WorkspaceMapper workspaceMapper;

  public Page<WorkspaceDto> getAllWorkspaces(Pageable pageable) {
    return workspaceRepository.findAll(pageable)
        .map(workspaceMapper::toDto);
  }

  public WorkspaceDto getWorkspaceById(Long id) {
    Workspace workspace = workspaceRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + id));
    return workspaceMapper.toDto(workspace);
  }

  @Transactional
  public WorkspaceDto createWorkspace(WorkspaceDto workspaceDto) {
    if (workspaceRepository.existsByNumber(workspaceDto.getNumber())) {
      throw new RuntimeException(
          "Workspace with number " + workspaceDto.getNumber() + " already exists");
    }

    Workspace workspace = workspaceMapper.toEntity(workspaceDto);

    if (workspaceDto.getAmenities() != null && !workspaceDto.getAmenities().isEmpty()) {
      List<Long> amenityIds = workspaceDto.getAmenities().stream()
          .map(WorkspaceDto.AmenityDto::getId).collect(Collectors.toList());
      List<Amenity> amenities = amenityRepository.findAllById(amenityIds);
      workspace.setAmenities(amenities);
    }

    Workspace savedWorkspace = workspaceRepository.save(workspace);
    return workspaceMapper.toDto(savedWorkspace);
  }

  @Transactional
  public WorkspaceDto updateWorkspace(Long id, WorkspaceDto workspaceDto) {
    Workspace workspace = workspaceRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + id));

    if (!workspace.getNumber().equals(workspaceDto.getNumber())
        && workspaceRepository.existsByNumber(workspaceDto.getNumber())) {
      throw new RuntimeException(
          "Workspace with number " + workspaceDto.getNumber() + " already exists");
    }

    workspace.setNumber(workspaceDto.getNumber());
    workspace.setCapacity(workspaceDto.getCapacity());
    workspace.setPricePerHour(workspaceDto.getPricePerHour());

    return getWorkspaceDto(workspaceDto, workspace);
  }

  private WorkspaceDto getWorkspaceDto(WorkspaceDto workspaceDto, Workspace workspace) {
    if (workspaceDto.getAmenities() != null) {
      List<Long> amenityIds = workspaceDto.getAmenities().stream()
          .map(WorkspaceDto.AmenityDto::getId).collect(Collectors.toList());
      List<Amenity> amenities = amenityRepository.findAllById(amenityIds);
      workspace.setAmenities(amenities);
    }

    Workspace updatedWorkspace = workspaceRepository.save(workspace);
    return workspaceMapper.toDto(updatedWorkspace);
  }

  @Transactional
  public WorkspaceDto partialUpdateWorkspace(Long id, WorkspaceDto workspaceDto) {
    Workspace workspace = workspaceRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + id));

    if (workspaceDto.getNumber() != null && !workspace.getNumber().equals(workspaceDto.getNumber())
        && workspaceRepository.existsByNumber(workspaceDto.getNumber())) {
      throw new RuntimeException(
          "Workspace with number " + workspaceDto.getNumber() + " already exists");
    }

    workspaceMapper.updateEntity(workspaceDto, workspace);

    return workspaceMapper.toDto(workspace);
  }

  @Transactional
  public void deleteWorkspace(Long id) {
    Workspace workspace = workspaceRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + id));

    boolean hasActiveBookings = workspace.getBookings() != null && workspace.getBookings().stream()
        .anyMatch(booking -> booking.getStatus() == BookingStatus.CONFIRMED);

    if (hasActiveBookings) {
      throw new RuntimeException("Cannot delete workspace with active bookings");
    }

    workspaceRepository.delete(workspace);
  }

  public List<WorkspaceDto> findAvailableWorkspaces(Integer minCapacity, Double maxPrice,
      List<Long> amenityIds) {

    BigDecimal maxPriceDecimal = maxPrice != null ? BigDecimal.valueOf(maxPrice) : null;

    List<Workspace> workspaces = workspaceRepository.findAvailableWorkspaces(minCapacity,
        maxPriceDecimal);

    if (amenityIds != null && !amenityIds.isEmpty()) {
      workspaces = workspaces.stream().filter(
          w -> w.getAmenities() != null && w.getAmenities().stream().map(Amenity::getId)
              .collect(Collectors.toSet()).containsAll(amenityIds)).toList();
    }

    return workspaceMapper.toDtoList(workspaces);
  }

  public List<WorkspaceDto> getWorkspacesByMinCapacity(Integer minCapacity) {
    return workspaceMapper.toDtoList(
        workspaceRepository.findByCapacityGreaterThanEqual(minCapacity));
  }

  public List<WorkspaceDto> getWorkspacesByMaxPrice(Double maxPrice) {
    return workspaceMapper.toDtoList(
        workspaceRepository.findByPricePerHourLessThanEqual(BigDecimal.valueOf(maxPrice)));
  }

  @Transactional
  public WorkspaceDto addAmenityToWorkspace(Long workspaceId, Long amenityId) {
    Workspace workspace = workspaceRepository.findById(workspaceId)
        .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + workspaceId));

    Amenity amenity = amenityRepository.findById(amenityId)
        .orElseThrow(() -> new RuntimeException("Amenity not found with id: " + amenityId));

    if (workspace.getAmenities() == null) {
      workspace.setAmenities(new java.util.ArrayList<>());
    }

    if (!workspace.getAmenities().contains(amenity)) {
      workspace.getAmenities().add(amenity);
    }

    Workspace updatedWorkspace = workspaceRepository.save(workspace);
    return workspaceMapper.toDto(updatedWorkspace);
  }

  @Transactional
  public WorkspaceDto removeAmenityFromWorkspace(Long workspaceId, Long amenityId) {
    Workspace workspace = workspaceRepository.findById(workspaceId)
        .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + workspaceId));

    if (workspace.getAmenities() != null) {
      workspace.getAmenities().removeIf(a -> a.getId().equals(amenityId));
    }

    Workspace updatedWorkspace = workspaceRepository.save(workspace);
    return workspaceMapper.toDto(updatedWorkspace);
  }

  public boolean existsByNumber(Integer number) {
    return workspaceRepository.existsByNumber(number);
  }

  @Transactional
  public void deleteByNumber(Integer number) {
    Workspace workspace = workspaceRepository.findByNumber(number)
        .orElseThrow(() -> new RuntimeException("Workspace not found with number: " + number));

    boolean hasActiveBookings = workspace.getBookings() != null && workspace.getBookings().stream()
        .anyMatch(booking -> booking.getStatus() == BookingStatus.CONFIRMED);

    if (hasActiveBookings) {
      throw new RuntimeException("Cannot delete workspace with active bookings");
    }

    workspaceRepository.deleteByNumber(number);
  }
}