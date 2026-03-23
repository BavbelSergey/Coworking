package com.example.coworking.mapper;

import com.example.coworking.dto.WorkspaceDto;
import com.example.coworking.model.Amenity;
import com.example.coworking.model.Workspace;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapper {

  public WorkspaceDto toDto(Workspace workspace) {
    if (workspace == null) {
      return null;
    }

    return WorkspaceDto.builder()
        .id(workspace.getId())
        .number(workspace.getNumber())
        .capacity(workspace.getCapacity())
        .pricePerHour(workspace.getPricePerHour())
        .amenities(mapAmenitiesToDto(workspace.getAmenities()))
        .build();
  }

  public Workspace toEntity(WorkspaceDto dto) {
    if (dto == null) {
      return null;
    }

    return Workspace.builder()
        .id(dto.getId())
        .number(dto.getNumber())
        .capacity(dto.getCapacity())
        .pricePerHour(dto.getPricePerHour())
        .build();
  }

  public void updateEntity(WorkspaceDto dto, Workspace workspace) {
    if (dto == null || workspace == null) {
      return;
    }

    if (dto.getNumber() != null) {
      workspace.setNumber(dto.getNumber());
    }
    if (dto.getCapacity() != null) {
      workspace.setCapacity(dto.getCapacity());
    }
    if (dto.getPricePerHour() != null) {
      workspace.setPricePerHour(dto.getPricePerHour());
    }
  }

  public List<WorkspaceDto> toDtoList(List<Workspace> workspaces) {
    if (workspaces == null) {
      return null;
    }
    return workspaces.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  private List<WorkspaceDto.AmenityDto> mapAmenitiesToDto(List<Amenity> amenities) {
    if (amenities == null) {
      return null;
    }
    return amenities.stream()
        .map(this::mapAmenityToDto)
        .toList();
  }

  private WorkspaceDto.AmenityDto mapAmenityToDto(Amenity amenity) {
    if (amenity == null) {
      return null;
    }

    return WorkspaceDto.AmenityDto.builder()
        .id(amenity.getId())
        .name(amenity.getName())
        .build();
  }
}