package com.example.coworking.service;

import com.example.coworking.dto.CoworkingRequest;
import com.example.coworking.dto.CoworkingResponse;
import com.example.coworking.mapper.CoworkingMapper;
import com.example.coworking.model.Coworking;
import com.example.coworking.repository.CoworkingRepository;
import java.util.List;
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
    return null;
  }

  @Override
  public List<CoworkingResponse> getAllCoworkings() {
    return List.of();
  }

  @Override
  public CoworkingResponse updateCoworking(Long id, CoworkingRequest request) {
    return null;
  }

  @Override
  public void deleteCoworking(Long id) {

  }
}
