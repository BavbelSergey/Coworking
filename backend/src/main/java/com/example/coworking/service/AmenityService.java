package com.example.coworking.service;

import com.example.coworking.dto.AmenityCreateDto;
import com.example.coworking.dto.AmenityDto;
import com.example.coworking.dto.AmenityUpdateDto;
import com.example.coworking.exception.ConflictException;
import com.example.coworking.exception.ErrorCode;
import com.example.coworking.exception.NotFoundException;
import com.example.coworking.mapper.AmenityMapper;
import com.example.coworking.model.Amenity;
import com.example.coworking.repository.AmenityRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AmenityService {

  private final AmenityRepository amenityRepository;
  private final AmenityMapper amenityMapper;

  public Page<AmenityDto> getAllAmenities(Pageable pageable) {
    log.debug("Fetching all amenities with pageable: {}", pageable);
    Page<AmenityDto> result = amenityRepository.findAll(pageable).map(amenityMapper::toDto);
    log.debug("Found {} amenities", result.getTotalElements());
    return result;
  }

  public AmenityDto getAmenityById(Long id) {
    log.debug("Fetching amenity by id: {}", id);
    Amenity amenity = amenityRepository.findById(id).orElseThrow(() -> {
      log.warn("Amenity not found with id: {}", id);
      return new NotFoundException(ErrorCode.AMENITY_NOT_FOUND);
    });
    log.info("Successfully fetched amenity: id={}, name={}", id, amenity.getName());
    return amenityMapper.toDto(amenity);
  }

  public AmenityDto getAmenityByName(String name) {
    log.debug("Fetching amenity by name: {}", name);
    Amenity amenity = amenityRepository.findByName(name).orElseThrow(() -> {
      log.warn("Amenity not found with name: {}", name);
      return new NotFoundException(ErrorCode.AMENITY_NOT_FOUND);
    });
    log.info("Successfully fetched amenity: name={}, id={}", name, amenity.getId());
    return amenityMapper.toDto(amenity);
  }

  @Transactional
  public AmenityDto createAmenity(AmenityCreateDto createDto) {
    log.info("Creating new amenity: name={}", createDto.getName());

    if (amenityRepository.existsByName(createDto.getName())) {
      log.warn("Attempt to create amenity with existing name: {}", createDto.getName());
      throw new ConflictException(ErrorCode.AMENITY_EXISTS);
    }

    Amenity amenity = amenityMapper.toEntity(createDto);
    Amenity savedAmenity = amenityRepository.save(amenity);
    log.info("Successfully created amenity: id={}, name={}", savedAmenity.getId(),
        savedAmenity.getName());
    return amenityMapper.toDto(savedAmenity);
  }

  @Transactional
  public AmenityDto updateAmenity(Long id, AmenityUpdateDto updateDto) {
    log.info("Updating amenity: id={}, updateData={}", id, updateDto);

    Amenity amenity = amenityRepository.findById(id).orElseThrow(() -> {
      log.warn("Cannot update - amenity not found with id: {}", id);
      return new NotFoundException(ErrorCode.AMENITY_NOT_FOUND);
    });

    if (updateDto.getName() != null && !updateDto.getName().equals(amenity.getName())
        && amenityRepository.existsByName(updateDto.getName())) {
      log.warn("Cannot update amenity id={} - name '{}' already exists", id, updateDto.getName());
      throw new ConflictException(ErrorCode.AMENITY_EXISTS);
    }

    String oldName = amenity.getName();
    amenityMapper.updateEntity(updateDto, amenity);
    Amenity updatedAmenity = amenityRepository.save(amenity);
    log.info("Successfully updated amenity: id={}, oldName={}, newName={}", id, oldName,
        updatedAmenity.getName());
    return amenityMapper.toDto(updatedAmenity);
  }

  @Transactional
  public void deleteAmenity(Long id) {
    log.info("Attempting to delete amenity by id: {}", id);

    Amenity amenity = amenityRepository.findById(id).orElseThrow(() -> {
      log.warn("Cannot delete - amenity not found with id: {}", id);
      return new NotFoundException(ErrorCode.AMENITY_NOT_FOUND);
    });

    if (!amenity.getWorkspaces().isEmpty()) {
      log.warn("Cannot delete amenity id={}, name={} - still used in {} workspaces", id,
          amenity.getName(), amenity.getWorkspaces().size());
      throw new ConflictException(ErrorCode.AMENITY_IS_USED_IN_WORKSPACE);
    }

    amenityRepository.delete(amenity);
    log.info("Successfully deleted amenity: id={}, name={}", id, amenity.getName());
  }

  @Transactional
  public void deleteAmenityByName(String name) {
    log.info("Attempting to delete amenity by name: {}", name);

    Amenity amenity = amenityRepository.findByName(name).orElseThrow(() -> {
      log.warn("Cannot delete - amenity not found with name: {}", name);
      return new NotFoundException(ErrorCode.AMENITY_NOT_FOUND);
    });

    if (!amenity.getWorkspaces().isEmpty()) {
      log.warn("Cannot delete amenity name={}, id={} - still used in {} workspaces", name,
          amenity.getId(), amenity.getWorkspaces().size());
      throw new ConflictException(ErrorCode.AMENITY_IS_USED_IN_WORKSPACE);
    }

    amenityRepository.deleteByName(name);
    log.info("Successfully deleted amenity by name: {}", name);
  }

  public List<AmenityDto> searchAmenitiesByName(String name) {
    log.debug("Searching amenities by name containing: {}", name);
    List<AmenityDto> result = amenityMapper.toDtoList(
        amenityRepository.findByNameContainingIgnoreCase(name));
    log.debug("Found {} amenities matching name '{}'", result.size(), name);
    return result;
  }

  public List<AmenityDto> searchAmenitiesByDescription(String description) {
    log.debug("Searching amenities by description containing: {}", description);
    List<AmenityDto> result = amenityMapper.toDtoList(
        amenityRepository.findByDescriptionContainingIgnoreCase(description));
    log.debug("Found {} amenities matching description", result.size());
    return result;
  }

  public List<AmenityDto> getAmenitiesByMinCapacity(Integer minCapacity) {
    log.debug("Fetching amenities by min workspace capacity: {}", minCapacity);
    List<AmenityDto> result = amenityMapper.toDtoList(
        amenityRepository.findByWorkspaceMinCapacity(minCapacity));
    log.debug("Found {} amenities with min capacity {}", result.size(), minCapacity);
    return result;
  }

  public List<AmenityDto> getAmenitiesByMaxPrice(Double maxPrice) {
    log.debug("Fetching amenities by max workspace price: {}", maxPrice);
    List<AmenityDto> result = amenityMapper.toDtoList(
        amenityRepository.findByWorkspaceMaxPrice(BigDecimal.valueOf(maxPrice)));
    log.debug("Found {} amenities with max price {}", result.size(), maxPrice);
    return result;
  }

  public List<String> getAllAmenityNames() {
    log.debug("Fetching all amenity names");
    List<String> names = amenityRepository.findAllNames();
    log.debug("Found {} amenity names", names.size());
    return names;
  }

  public List<AmenityDto> getAmenitiesNotInWorkspace(Long workspaceId) {
    log.debug("Fetching amenities not in workspace: {}", workspaceId);
    List<AmenityDto> result = amenityMapper.toDtoList(
        amenityRepository.findNotInWorkspace(workspaceId));
    log.debug("Found {} amenities not in workspace {}", result.size(), workspaceId);
    return result;
  }

  public List<Object[]> getAmenityStatistics() {
    log.debug("Fetching amenity statistics");
    List<Object[]> statistics = amenityRepository.countWorkspaces();
    log.debug("Fetched statistics for {} amenities", statistics.size());
    return statistics;
  }
}