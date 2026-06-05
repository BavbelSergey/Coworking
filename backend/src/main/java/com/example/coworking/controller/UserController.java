package com.example.coworking.controller;

import com.example.coworking.dto.UserCreateDto;
import com.example.coworking.dto.UserDto;
import com.example.coworking.dto.UserUpdateDto;
import com.example.coworking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Users", description = "Управление пользователями коворкинга")
public class UserController {

  private final UserService userService;

  @Operation(
      summary = "Получить список пользователей",
      description = "Возвращает всех пользователей с пагинацией и сортировкой по имени"
  )
  @GetMapping
  public Page<UserDto> getAllUsers(
      @Parameter(hidden = true) Pageable pageable
  ) {
    return userService.getAllUsers(pageable);
  }

  @Operation(
      summary = "Получить пользователя по ID",
      description = "Возвращает детальную информацию о пользователе"
  )
  @GetMapping("/{id}")
  public UserDto getUserById(
      @Parameter(description = "ID пользователя", example = "1")
      @PathVariable @Positive(message = "User ID must be a positive number") Long id
  ) {
    return userService.getUserById(id);
  }

  @Operation(
      summary = "Получить пользователя по email",
      description = "Возвращает пользователя по его email"
  )
  @GetMapping("/email/{email}")
  public UserDto getUserByEmail(
      @Parameter(description = "Email пользователя", example = "user@example.com")
      @PathVariable @Email(message = "Invalid email format")
      @NotBlank(message = "Email cannot be blank") String email
  ) {
    return userService.getUserByEmail(email);
  }

  @Operation(
      summary = "Создать пользователя",
      description = "Создаёт нового пользователя. Email и телефон должны быть уникальными"
  )
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto createUser(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Данные для создания пользователя",
          required = true
      )
      @Valid @RequestBody UserCreateDto createDto
  ) {
    return userService.createUser(createDto);
  }

  @Operation(
      summary = "Обновить пользователя",
      description = "Полностью обновляет данные пользователя по ID. Email и телефон должны быть уникальными"
  )
  @PutMapping("/{id}")
  public UserDto updateUser(
      @Parameter(description = "ID пользователя", example = "1")
      @PathVariable @Positive(message = "User ID must be a positive number") Long id,

      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Новые данные пользователя",
          required = true
      )
      @Valid @RequestBody UserUpdateDto updateDto
  ) {
    return userService.updateUser(id, updateDto);
  }

  @Operation(
      summary = "Удалить пользователя",
      description = "Удаляет пользователя. Нельзя удалить пользователя с активными бронированиями"
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(
      @Parameter(description = "ID пользователя", example = "1")
      @PathVariable @Positive(message = "User ID must be a positive number") Long id
  ) {
    userService.deleteUser(id);
  }

  @Operation(
      summary = "Поиск пользователей по имени",
      description = "Возвращает пользователей, чьё имя содержит указанную строку (без учёта регистра)"
  )
  @GetMapping("/search")
  public List<UserDto> searchUsersByName(
      @Parameter(description = "Строка для поиска в имени", example = "Ivan")
      @RequestParam @NotBlank(message = "Search name cannot be blank") String name
  ) {
    return userService.searchUsersByName(name);
  }

  @Operation(
      summary = "Пользователи с активными бронированиями",
      description = "Возвращает пользователей, у которых есть бронирования в статусе PENDING"
  )
  @GetMapping("/active-bookings")
  public List<UserDto> getUsersWithActiveBookings() {
    return userService.getUsersWithActiveBookings();
  }

  @Operation(
      summary = "Пользователи без бронирований",
      description = "Возвращает пользователей, у которых нет ни одного бронирования"
  )
  @GetMapping("/without-bookings")
  public List<UserDto> getUsersWithoutBookings() {
    return userService.getUsersWithoutBookings();
  }

  @Operation(
      summary = "Проверить существование email",
      description = "Возвращает true, если пользователь с таким email уже зарегистрирован"
  )
  @GetMapping("/exists/email/{email}")
  public Boolean existsByEmail(
      @Parameter(description = "Email для проверки", example = "user@example.com")
      @PathVariable @Email(message = "Invalid email format")
      @NotBlank(message = "Email cannot be blank") String email
  ) {
    return userService.existsByEmail(email);
  }
}