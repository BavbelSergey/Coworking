package com.example.coworking.controller;

import com.example.coworking.dto.CoworkingResponse;
import com.example.coworking.service.CoworkingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coworking")
@RequiredArgsConstructor
public class CoworkingController {

  private final CoworkingService coworkingService;

  @GetMapping("/search")
  public ResponseEntity<List<CoworkingResponse>> getCoworkingsByPrice(
      @RequestParam(value = "price", required = false) Double price) {

    if (price != null) {
      return ResponseEntity.ok(coworkingService.getCoworkingsByPrice(price));
    }
    return ResponseEntity.ok(coworkingService.getAllCoworkings());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CoworkingResponse> getCoworkingById(@PathVariable Long id) {
    return ResponseEntity.ok(coworkingService.getCoworkingById(id));
  }
}
