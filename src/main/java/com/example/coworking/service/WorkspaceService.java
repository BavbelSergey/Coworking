package com.example.coworking.service;

import com.example.coworking.dto.AmenityDto;
import com.example.coworking.dto.WorkspaceDto;
import com.example.coworking.exception.ConflictException;
import com.example.coworking.exception.ErrorCode;
import com.example.coworking.exception.NotFoundException;
import com.example.coworking.mapper.WorkspaceMapper;
import com.example.coworking.model.Amenity;
import com.example.coworking.model.BookingStatus;
import com.example.coworking.model.Workspace;
import com.example.coworking.repository.AmenityRepository;
import com.example.coworking.repository.WorkspaceRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceService {

  private final WorkspaceRepository workspaceRepository;
  private final AmenityRepository amenityRepository;
  private final WorkspaceMapper workspaceMapper;

  public Page<WorkspaceDto> getAllWorkspaces(Pageable pageable) {
    log.debug("Fetching all workspaces with pageable: {}", pageable);
    Page<WorkspaceDto> result = workspaceRepository.findAll(pageable).map(workspaceMapper::toDto);
    log.debug("Found {} workspaces", result.getTotalElements());
    return result;
  }

  public WorkspaceDto getWorkspaceById(Long id) {
    log.debug("Fetching workspace by id: {}", id);
    Optional<Workspace> workspaceOpt = workspaceRepository.findById(id);
    if (workspaceOpt.isEmpty()) {
      log.warn("Workspace not found with id: {}", id);
      throw new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND);
    }
    Workspace workspace = workspaceOpt.get();
    log.info("Successfully fetched workspace: id={}, number={}", id, workspace.getNumber());
    return workspaceMapper.toDto(workspace);
  }

  @Transactional
  public WorkspaceDto createWorkspace(WorkspaceDto workspaceDto) {
    log.info("Creating new workspace: number={}, capacity={}, pricePerHour={}",
        workspaceDto.getNumber(), workspaceDto.getCapacity(), workspaceDto.getPricePerHour());

    if (workspaceRepository.existsByNumber(workspaceDto.getNumber())) {
      log.warn("Cannot create workspace — number already exists: {}", workspaceDto.getNumber());
      throw new ConflictException(ErrorCode.WORKSPACE_EXISTS_WITH_NUMBER);
    }

    Workspace workspace = workspaceMapper.toEntity(workspaceDto);

    if (workspaceDto.getAmenities() != null && !workspaceDto.getAmenities().isEmpty()) {
      List<Long> amenityIds = workspaceDto.getAmenities().stream()
          .map(AmenityDto::getId)
          .collect(Collectors.toList());
      List<Amenity> amenities = amenityRepository.findAllById(amenityIds);
      workspace.setAmenities(amenities);
      log.debug("Attached {} amenities to workspace", amenities.size());
    }

    Workspace savedWorkspace = workspaceRepository.save(workspace);
    log.info("Successfully created workspace: id={}, number={}, capacity={}",
        savedWorkspace.getId(), savedWorkspace.getNumber(), savedWorkspace.getCapacity());
    return workspaceMapper.toDto(savedWorkspace);
  }

  @Transactional
  public WorkspaceDto updateWorkspace(Long id, WorkspaceDto workspaceDto) {
    log.info("Updating workspace (full): id={}, number={}, capacity={}, pricePerHour={}",
        id, workspaceDto.getNumber(), workspaceDto.getCapacity(), workspaceDto.getPricePerHour());

    Optional<Workspace> workspaceOpt = workspaceRepository.findById(id);
    if (workspaceOpt.isEmpty()) {
      log.warn("Cannot update — workspace not found: id={}", id);
      throw new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND);
    }
    Workspace workspace = workspaceOpt.get();

    if (!workspace.getNumber().equals(workspaceDto.getNumber())
        && workspaceRepository.existsByNumber(workspaceDto.getNumber())) {
      log.warn("Cannot update workspace id={} — number already taken: {}",
          id, workspaceDto.getNumber());
      throw new ConflictException(ErrorCode.WORKSPACE_EXISTS_WITH_NUMBER);
    }

    workspace.setNumber(workspaceDto.getNumber());
    workspace.setCapacity(workspaceDto.getCapacity());
    workspace.setPricePerHour(workspaceDto.getPricePerHour());
    Integer oldNumber = workspace.getNumber();

    WorkspaceDto result = getWorkspaceDto(workspaceDto, workspace);
    log.info("Successfully updated workspace (full): id={}, oldNumber={}, newNumber={}",
        id, oldNumber, workspaceDto.getNumber());
    return result;
  }

  private WorkspaceDto getWorkspaceDto(WorkspaceDto workspaceDto, Workspace workspace) {
    if (workspaceDto.getAmenities() != null) {
      List<Long> amenityIds = workspaceDto.getAmenities().stream()
          .map(AmenityDto::getId).toList();
      List<Amenity> amenities = amenityRepository.findAllById(amenityIds);
      workspace.setAmenities(amenities);
    }

    Workspace updatedWorkspace = workspaceRepository.save(workspace);
    return workspaceMapper.toDto(updatedWorkspace);
  }

  @Transactional
  public WorkspaceDto partialUpdateWorkspace(Long id, WorkspaceDto workspaceDto) {
    log.info("Partially updating workspace: id={}, updateData={}", id, workspaceDto);

    Optional<Workspace> workspaceOpt = workspaceRepository.findById(id);
    if (workspaceOpt.isEmpty()) {
      log.warn("Cannot partially update — workspace not found: id={}", id);
      throw new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND);
    }
    Workspace workspace = workspaceOpt.get();

    if (workspaceDto.getNumber() != null
        && !workspace.getNumber().equals(workspaceDto.getNumber())
        && workspaceRepository.existsByNumber(workspaceDto.getNumber())) {
      log.warn("Cannot partially update workspace id={} — number already taken: {}",
          id, workspaceDto.getNumber());
      throw new ConflictException(ErrorCode.WORKSPACE_EXISTS_WITH_NUMBER);
    }

    Integer oldNumber = workspace.getNumber();
    workspaceMapper.updateEntity(workspaceDto, workspace);
    Workspace updatedWorkspace = workspaceRepository.save(workspace);
    log.info("Successfully partially updated workspace: id={}, oldNumber={}, newNumber={}",
        id, oldNumber, updatedWorkspace.getNumber());
    return workspaceMapper.toDto(updatedWorkspace);
  }

  @Transactional
  public void deleteWorkspace(Long id) {
    log.info("Attempting to delete workspace: id={}", id);

    Optional<Workspace> workspaceOpt = workspaceRepository.findById(id);
    if (workspaceOpt.isEmpty()) {
      log.warn("Cannot delete — workspace not found: id={}", id);
      throw new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND);
    }
    Workspace workspace = workspaceOpt.get();

    boolean hasActiveBookings = workspace.getBookings() != null
        && workspace.getBookings().stream()
        .anyMatch(booking -> booking.getStatus() == BookingStatus.CONFIRMED);

    if (hasActiveBookings) {
      log.warn("Cannot delete workspace id={}, number={} — has active (CONFIRMED) bookings",
          id, workspace.getNumber());
      throw new ConflictException(ErrorCode.WORKSPACE_HAS_ACTIVE_BOOKINGS);
    }

    workspaceRepository.delete(workspace);
    log.info("Successfully deleted workspace: id={}, number={}", id, workspace.getNumber());
  }

  public List<WorkspaceDto> findAvailableWorkspaces(Integer minCapacity, Double maxPrice,
      List<Long> amenityIds) {
    log.debug("Finding available workspaces: minCapacity={}, maxPrice={}, amenityIds={}",
        minCapacity, maxPrice, amenityIds);

    BigDecimal maxPriceDecimal = maxPrice != null ? BigDecimal.valueOf(maxPrice) : null;

    List<Workspace> workspaces = workspaceRepository.findAvailableWorkspaces(
        minCapacity, maxPriceDecimal);

    if (amenityIds != null && !amenityIds.isEmpty()) {
      workspaces = workspaces.stream()
          .filter(w -> w.getAmenities() != null
              && w.getAmenities().stream()
              .map(Amenity::getId)
              .collect(Collectors.toSet())
              .containsAll(amenityIds))
          .toList();
    }

    return workspaceMapper.toDtoList(workspaces);
  }

  public List<WorkspaceDto> getWorkspacesByMinCapacity(Integer minCapacity) {
    log.debug("Fetching workspaces by min capacity: {}", minCapacity);
    List<WorkspaceDto> result = workspaceMapper.toDtoList(
        workspaceRepository.findByCapacityGreaterThanEqual(minCapacity));
    log.debug("Found {} workspaces with min capacity {}", result.size(), minCapacity);
    return result;
  }

  public List<WorkspaceDto> getWorkspacesByMaxPrice(Double maxPrice) {
    log.debug("Fetching workspaces by max price: {}", maxPrice);
    List<WorkspaceDto> result = workspaceMapper.toDtoList(
        workspaceRepository.findByPricePerHourLessThanEqual(BigDecimal.valueOf(maxPrice)));
    log.debug("Found {} workspaces with max price {}", result.size(), maxPrice);
    return result;
  }

  @Transactional
  public WorkspaceDto addAmenityToWorkspace(Long workspaceId, Long amenityId) {
    log.info("Adding amenity {} to workspace {}", amenityId, workspaceId);

    Optional<Workspace> workspaceOpt = workspaceRepository.findById(workspaceId);
    if (workspaceOpt.isEmpty()) {
      log.warn("Cannot add amenity — workspace not found: id={}", workspaceId);
      throw new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND);
    }
    Workspace workspace = workspaceOpt.get();

    Optional<Amenity> amenityOpt = amenityRepository.findById(amenityId);
    if (amenityOpt.isEmpty()) {
      log.warn("Cannot add amenity — amenity not found: id={}", amenityId);
      throw new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND);
    }
    Amenity amenity = amenityOpt.get();

    if (workspace.getAmenities() == null) {
      workspace.setAmenities(new ArrayList<>());
    }

    if (!workspace.getAmenities().contains(amenity)) {
      workspace.getAmenities().add(amenity);
      Workspace updatedWorkspace = workspaceRepository.save(workspace);
      log.info("Successfully added amenity {} to workspace {}", amenityId, workspaceId);
      return workspaceMapper.toDto(updatedWorkspace);
    }

    log.debug("Amenity {} already exists in workspace {}, skipping", amenityId, workspaceId);
    return workspaceMapper.toDto(workspace);
  }

  @Transactional
  public WorkspaceDto removeAmenityFromWorkspace(Long workspaceId, Long amenityId) {
    log.info("Removing amenity {} from workspace {}", amenityId, workspaceId);

    Optional<Workspace> workspaceOpt = workspaceRepository.findById(workspaceId);
    if (workspaceOpt.isEmpty()) {
      log.warn("Cannot remove amenity — workspace not found: id={}", workspaceId);
      throw new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND);
    }
    Workspace workspace = workspaceOpt.get();

    if (workspace.getAmenities() != null) {
      boolean removed = workspace.getAmenities().removeIf(a -> a.getId().equals(amenityId));
      if (removed) {
        Workspace updatedWorkspace = workspaceRepository.save(workspace);
        log.info("Successfully removed amenity {} from workspace {}", amenityId, workspaceId);
        return workspaceMapper.toDto(updatedWorkspace);
      }
    }

    log.debug("Amenity {} was not in workspace {}, nothing to remove", amenityId, workspaceId);
    return workspaceMapper.toDto(workspace);
  }

  public boolean existsByNumber(Integer number) {
    log.debug("Checking if workspace exists by number: {}", number);
    boolean exists = workspaceRepository.existsByNumber(number);
    log.debug("Workspace with number {} exists: {}", number, exists);
    return exists;
  }

  @Transactional
  public void deleteByNumber(Integer number) {
    log.info("Attempting to delete workspace by number: {}", number);

    Optional<Workspace> workspaceOpt = workspaceRepository.findByNumber(number);
    if (workspaceOpt.isEmpty()) {
      log.warn("Cannot delete — workspace not found by number: {}", number);
      throw new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND);
    }
    Workspace workspace = workspaceOpt.get();

    boolean hasActiveBookings = workspace.getBookings() != null
        && workspace.getBookings().stream()
        .anyMatch(booking -> booking.getStatus() == BookingStatus.CONFIRMED);

    if (hasActiveBookings) {
      log.warn("Cannot delete workspace number={}, id={} — has active (CONFIRMED) bookings",
          number, workspace.getId());
      throw new ConflictException(ErrorCode.WORKSPACE_HAS_ACTIVE_BOOKINGS);
    }

    workspaceRepository.deleteByNumber(number);
    log.info("Successfully deleted workspace by number: {}", number);
  }
}