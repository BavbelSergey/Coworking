package com.example.coworking.mapper;

import com.example.coworking.dto.AmenityCreateDto;
import com.example.coworking.dto.AmenityDto;
import com.example.coworking.dto.AmenityUpdateDto;
import com.example.coworking.model.Amenity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AmenityMapper {

  public AmenityDto toDto(Amenity amenity) {
    if (amenity == null) {
      return null;
    }

    AmenityDto dto = new AmenityDto();
    dto.setId(amenity.getId());
    dto.setName(amenity.getName());
    dto.setDescription(amenity.getDescription());

    if (amenity.getWorkspaces() != null) {
      dto.setWorkspacesCount(amenity.getWorkspaces().size());
    }

    return dto;
  }

  public Amenity toEntity(AmenityCreateDto createDto) {
    if (createDto == null) {
      return null;
    }

    Amenity amenity = new Amenity();
    amenity.setName(createDto.getName());
    amenity.setDescription(createDto.getDescription());

    return amenity;
  }

  public void updateEntity(AmenityUpdateDto updateDto, Amenity amenity) {
    if (updateDto == null || amenity == null) {
      return;
    }

    if (updateDto.getName() != null) {
      amenity.setName(updateDto.getName());
    }
    if (updateDto.getDescription() != null) {
      amenity.setDescription(updateDto.getDescription());
    }
  }

  public List<AmenityDto> toDtoList(List<Amenity> amenities) {
    if (amenities == null) {
      return null;
    }
    return amenities.stream().map(this::toDto).collect(Collectors.toList());
  }
}