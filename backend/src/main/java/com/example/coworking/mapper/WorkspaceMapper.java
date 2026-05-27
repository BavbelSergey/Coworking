package com.example.coworking.mapper;

import com.example.coworking.dto.AmenityDto;
import com.example.coworking.dto.WorkspaceDto;
import com.example.coworking.model.Amenity;
import com.example.coworking.model.Workspace;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapper {

  public WorkspaceDto toDto(Workspace workspace) {
    if (workspace == null) {
      return null;
    }

    return WorkspaceDto.builder().id(workspace.getId()).name(workspace.getName())
        .phoneNumber(workspace.getPhoneNumber()).capacity(workspace.getCapacity())
        .pricePerHour(workspace.getPricePerHour())
        .amenities(mapAmenitiesToDto(workspace.getAmenities())).build();
  }

  public Workspace toEntity(WorkspaceDto dto) {
    if (dto == null) {
      return null;
    }

    return Workspace.builder().id(dto.getId()).name(dto.getName()).phoneNumber(dto.getPhoneNumber())
        .capacity(dto.getCapacity()).pricePerHour(dto.getPricePerHour()).build();
  }

  public void updateEntity(WorkspaceDto dto, Workspace workspace) {
    if (dto == null || workspace == null) {
      return;
    }

    if (dto.getName() != null) {
      workspace.setName(dto.getName());
    }
    if (dto.getPhoneNumber() != null) {
      workspace.setPhoneNumber(dto.getPhoneNumber());
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
      return new ArrayList<>();
    }
    return workspaces.stream().map(this::toDto).toList();
  }

  private List<AmenityDto> mapAmenitiesToDto(List<Amenity> amenities) {
    if (amenities == null) {
      return new ArrayList<>();
    }
    return amenities.stream().map(this::mapAmenityToDto).toList();
  }

  private AmenityDto mapAmenityToDto(Amenity amenity) {
    if (amenity == null) {
      return new AmenityDto();
    }

    return AmenityDto.builder().id(amenity.getId()).name(amenity.getName()).build();
  }
}
