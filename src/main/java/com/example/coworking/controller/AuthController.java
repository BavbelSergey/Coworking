package com.example.coworking.controller;

import com.example.coworking.dto.AuthRequestDto;
import com.example.coworking.dto.AuthResponseDto;
import com.example.coworking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Аутентификация пользователей")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Войти и получить JWT")
  @PostMapping("/login")
  public AuthResponseDto login(@Valid @RequestBody AuthRequestDto request) {
    return authService.login(request);
  }
}
