package com.example.coworking.mapper;

import com.example.coworking.dto.CoworkingRequest;
import com.example.coworking.dto.CoworkingResponse;
import com.example.coworking.model.Coworking;
import java.util.HashSet;
import org.springframework.stereotype.Component;

@Component
public class CoworkingMapper {

  public Coworking toEntity(CoworkingRequest request) {
    if (request == null) {
      return null;
    }

    return Coworking.builder()
        .name(request.getName())
        .size(request.getSize())
        .price(request.getPrice())
        .address(request.getAddress())
        .phoneNumber(request.getPhoneNumber())
        .tags(request.getTags() != null
            ? new HashSet<>(request.getTags())
            : new HashSet<>())
        .build();
  }

  public CoworkingResponse toResponse(Coworking coworking) {
    if (coworking == null) {
      return null;
    }

    CoworkingResponse response = new CoworkingResponse();
    response.setId(coworking.getId());
    response.setName(coworking.getName());
    response.setSize(coworking.getSize());
    response.setPrice(coworking.getPrice());
    response.setAddress(coworking.getAddress());
    response.setPhoneNumber(coworking.getPhoneNumber());
    response.setTags(coworking.getTags() != null
        ? new HashSet<>(coworking.getTags())
        : new HashSet<>());

    return response;
  }

  public void updateEntity(CoworkingRequest request, Coworking coworking) {
    if (request == null || coworking == null) {
      return;
    }
    coworking.setName(request.getName());
    coworking.setSize(request.getSize());
    coworking.setPrice(request.getPrice());
    coworking.setAddress(request.getAddress());
    coworking.setPhoneNumber(request.getPhoneNumber());
    coworking.setTags(request.getTags() != null
        ? new HashSet<>(request.getTags())
        : new HashSet<>());
  }
}