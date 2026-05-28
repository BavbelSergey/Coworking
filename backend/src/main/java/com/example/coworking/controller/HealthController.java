package com.example.coworking.controller;

import com.example.coworking.dto.HealthResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Application healthcheck")
public class HealthController {

  @GetMapping
  @Operation(summary = "Check application health")
  public HealthResponseDto health() {
    return new HealthResponseDto("UP");
  }
}
