package com.example.coworking.controller;

import com.example.coworking.dto.UserCreateDto;
import com.example.coworking.dto.UserDto;
import com.example.coworking.dto.UserUpdateDto;
import com.example.coworking.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

  private final UserService userService;

  @GetMapping
  public ResponseEntity<Page<UserDto>> getAllUsers(
      @PageableDefault(size = 20, sort = "name") Pageable pageable) {
    return ResponseEntity.ok(userService.getAllUsers(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getUserById(
      @PathVariable @Positive(message = "User ID must be a positive number") Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<UserDto> getUserByEmail(
      @PathVariable @Email(message =
          "Invalid email format") @NotBlank(message = "Email cannot be blank") String email) {
    return ResponseEntity.ok(userService.getUserByEmail(email));
  }

  @PostMapping
  public ResponseEntity<UserDto> createUser(
      @Valid @RequestBody UserCreateDto createDto) {
    UserDto createdUser = userService.createUser(createDto);
    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserDto> updateUser(
      @PathVariable @Positive(message = "User ID must be a positive number") Long id,
      @Valid @RequestBody UserUpdateDto updateDto) {
    UserDto updatedUser = userService.updateUser(id, updateDto);
    return ResponseEntity.ok(updatedUser);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(
      @PathVariable @Positive(message = "User ID must be a positive number") Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public ResponseEntity<List<UserDto>> searchUsersByName(
      @RequestParam @NotBlank(message = "Search name cannot be blank") String name) {
    return ResponseEntity.ok(userService.searchUsersByName(name));
  }

  @GetMapping("/active-bookings")
  public ResponseEntity<List<UserDto>> getUsersWithActiveBookings() {
    return ResponseEntity.ok(userService.getUsersWithActiveBookings());
  }

  @GetMapping("/without-bookings")
  public ResponseEntity<List<UserDto>> getUsersWithoutBookings() {
    return ResponseEntity.ok(userService.getUsersWithoutBookings());
  }

  @GetMapping("/exists/email/{email}")
  public ResponseEntity<Boolean> existsByEmail(
      @PathVariable @Email(message =
          "Invalid email format") @NotBlank(message = "Email cannot be blank") String email) {
    return ResponseEntity.ok(userService.existsByEmail(email));
  }
}