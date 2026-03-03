package com.example.coworking.service;

import com.example.coworking.dto.CoworkingRequest;
import com.example.coworking.dto.CoworkingResponse;
import com.example.coworking.mapper.CoworkingMapper;
import com.example.coworking.model.Coworking;
import com.example.coworking.repository.CoworkingRepository;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoworkingServiceImpl implements CoworkingService {

  private final CoworkingRepository coworkingRepository;
  private final CoworkingMapper coworkingMapper;

  @Override
  public CoworkingResponse createCoworking(CoworkingRequest request) {
    Coworking coworking = coworkingMapper.toEntity(request);
    Coworking savedCoworking = coworkingRepository.save(coworking);
    return coworkingMapper.toResponse(savedCoworking);
  }

  @Override
  public CoworkingResponse getCoworkingById(Long id) {
    Coworking coworking = coworkingRepository.findById(id);
    if (coworking == null) {
      throw new RuntimeException("Coworking not found with id" + id);
    }
    return coworkingMapper.toResponse(coworking);
  }

  @Override
  public List<CoworkingResponse> getAllCoworkings() {
    return coworkingRepository.findAll().stream()
        .map(coworkingMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public CoworkingResponse updateCoworking(Long id, CoworkingRequest request) {
    Coworking existingCoworking = coworkingRepository.findById(id);
    if (existingCoworking == null) {
      throw new RuntimeException("Book not found with id: " + id);
    }

    existingCoworking.setName(request.getName());
    existingCoworking.setSize(request.getSize());
    existingCoworking.setPrice(request.getPrice());
    existingCoworking.setAddress(request.getAddress());
    existingCoworking.setPhoneNumber(request.getPhoneNumber());
    existingCoworking.setTags(request.getTags() != null
        ? new HashSet<>(request.getTags())
        : new HashSet<>());

    Coworking updatedCoworking = coworkingRepository.save(existingCoworking);
    return coworkingMapper.toResponse(updatedCoworking);
  }

  @Override
  public List<CoworkingResponse> getCoworkingsByPrice(Double price) {
    return coworkingRepository.findByPrice(price).stream()
        .map(coworkingMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteCoworking(Long id) {
    coworkingRepository.deleteById(id);
  }
}
