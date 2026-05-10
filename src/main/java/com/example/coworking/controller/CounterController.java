package com.example.coworking.controller;

import com.example.coworking.dto.CounterResponseDto;
import com.example.coworking.service.CounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/concurrency")
@RequiredArgsConstructor
@Validated
@Tag(name = "Concurrency Demo", description = "Endpoints for concurrency demonstrations")
public class CounterController {

  private final CounterService counterService;

  @GetMapping("/race")
  @Operation(summary = "Run race condition demo")
  public CounterResponseDto runCounter(
      int threads, int incrementsPerThread
  ) {
    return counterService.runCounter(threads, incrementsPerThread);
  }
}
