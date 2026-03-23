package com.example.coworking.service;

import com.example.coworking.dto.AmenityCreateDto;
import com.example.coworking.dto.AmenityDto;
import com.example.coworking.dto.AmenityUpdateDto;
import com.example.coworking.mapper.AmenityMapper;
import com.example.coworking.model.Amenity;
import com.example.coworking.repository.AmenityRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AmenityService {

  private final AmenityRepository amenityRepository;
  private final AmenityMapper amenityMapper;

  public List<AmenityDto> getAllAmenities() {
    return amenityMapper.toDtoList(amenityRepository.findAll());
  }

  public AmenityDto getAmenityById(Long id) {
    Amenity amenity = amenityRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Amenity not found with id: " + id));
    return amenityMapper.toDto(amenity);
  }

  public AmenityDto getAmenityByName(String name) {
    Amenity amenity = amenityRepository.findByName(name)
        .orElseThrow(() -> new RuntimeException("Amenity not found with name: " + name));
    return amenityMapper.toDto(amenity);
  }

  @Transactional
  public AmenityDto createAmenity(AmenityCreateDto createDto) {
    if (amenityRepository.existsByName(createDto.getName())) {
      throw new RuntimeException("Amenity with name " + createDto.getName() + " already exists");
    }

    Amenity amenity = amenityMapper.toEntity(createDto);
    Amenity savedAmenity = amenityRepository.save(amenity);
    return amenityMapper.toDto(savedAmenity);
  }

  @Transactional
  public AmenityDto updateAmenity(Long id, AmenityUpdateDto updateDto) {
    Amenity amenity = amenityRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Amenity not found with id: " + id));

    if (updateDto.getName() != null && !updateDto.getName().equals(amenity.getName())
        && amenityRepository.existsByName(updateDto.getName())) {
      throw new RuntimeException("Amenity with name " + updateDto.getName() + " already exists");
    }

    amenityMapper.updateEntity(updateDto, amenity);
    Amenity updatedAmenity = amenityRepository.save(amenity);
    return amenityMapper.toDto(updatedAmenity);
  }

  @Transactional
  public AmenityDto partialUpdateAmenity(Long id, AmenityUpdateDto updateDto) {
    return updateAmenity(id, updateDto);
  }

  @Transactional
  public void deleteAmenity(Long id) {
    Amenity amenity = amenityRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Amenity not found with id: " + id));

    if (!amenity.getWorkspaces().isEmpty()) {
      throw new RuntimeException(
          "Cannot delete amenity that is used in " + amenity.getWorkspaces().size()
              + " workspaces");
    }

    amenityRepository.delete(amenity);
  }

  @Transactional
  public void deleteAmenityByName(String name) {
    Amenity amenity = amenityRepository.findByName(name)
        .orElseThrow(() -> new RuntimeException("Amenity not found with name: " + name));

    if (!amenity.getWorkspaces().isEmpty()) {
      throw new RuntimeException("Cannot delete amenity that is used in workspaces");
    }

    amenityRepository.deleteByName(name);
  }

  public List<AmenityDto> searchAmenitiesByName(String name) {
    return amenityMapper.toDtoList(amenityRepository.findByNameContainingIgnoreCase(name));
  }

  public List<AmenityDto> searchAmenitiesByDescription(String description) {
    return amenityMapper.toDtoList(
        amenityRepository.findByDescriptionContainingIgnoreCase(description));
  }

  public List<AmenityDto> getAmenitiesByMinCapacity(Integer minCapacity) {
    return amenityMapper.toDtoList(amenityRepository.findByWorkspaceMinCapacity(minCapacity));
  }

  public List<AmenityDto> getAmenitiesByMaxPrice(Double maxPrice) {
    return amenityMapper.toDtoList(
        amenityRepository.findByWorkspaceMaxPrice(BigDecimal.valueOf(maxPrice)));
  }

  public List<String> getAllAmenityNames() {
    return amenityRepository.findAllNames();
  }

  public List<AmenityDto> getAmenitiesNotInWorkspace(Long workspaceId) {
    return amenityMapper.toDtoList(amenityRepository.findNotInWorkspace(workspaceId));
  }

  public List<Object[]> getAmenityStatistics() {
    return amenityRepository.countWorkspaces();
  }
}