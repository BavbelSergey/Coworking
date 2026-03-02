package com.example.coworking.service;

import com.example.coworking.dto.CoworkingRequest;
import com.example.coworking.dto.CoworkingResponse;
import java.util.List;

public interface CoworkingService {

  CoworkingResponse createCoworking(CoworkingRequest request);

  CoworkingResponse getCoworkingById(Long id);

  List<CoworkingResponse> getAllCoworkings();

  CoworkingResponse updateCoworking(Long id, CoworkingRequest request);

  void deleteCoworking(Long id);
}
