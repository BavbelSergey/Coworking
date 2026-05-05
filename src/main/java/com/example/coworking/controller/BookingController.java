package com.example.coworking.controller;

import com.example.coworking.dto.BookingCreateDto;
import com.example.coworking.dto.BookingDto;
import com.example.coworking.dto.BookingUpdateDto;
import com.example.coworking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Validated
@Tag(name = "Bookings", description = "Управление бронированиями рабочих мест")
public class BookingController {

  private final BookingService bookingService;

  @Operation(
      summary = "Массовое создание бронирований",
      description = "Создаёт несколько бронирований одним запросом"
  )
  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.CREATED)
  public List<BookingDto> createBookingsBulk(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Список бронирований для создания",
          required = true
      )
      @Valid @RequestBody List<@Valid BookingCreateDto> dtos
  ) {
    return bookingService.createBookingsBulk(dtos);
  }

  @Operation(
      summary = "Получить бронирования пользователя по фильтрам",
      description = "Возвращает бронирования, отфильтрованные по максимальной цене и "
          + "минимальной вместимости рабочего места. Результаты кешируются"
  )
  @GetMapping("/workspaces/")
  public Page<BookingDto> getUserBookings(
      @Parameter(description = "Максимальная цена рабочего места в час", example = "500")
      @RequestParam @Positive(message = "Max price must be positive") Long maxPrice,

      @Parameter(description = "Минимальная вместимость рабочего места", example = "2")
      @RequestParam @Min(value = 0, message = "Min capacity cannot be negative") Long minCapacity,

      @Parameter(hidden = true) Pageable pageable
  ) {
    return bookingService.getUserBookings(maxPrice, minCapacity, pageable);
  }

  @Operation(
      summary = "Получить бронирования пользователя (нативный запрос)",
      description = "То же, что /workspaces/, но через нативный SQL. Результаты кешируются"
  )
  @GetMapping("/workspaces-native/")
  public Page<BookingDto> getUserBookingsNative(
      @Parameter(description = "Максимальная цена рабочего места в час", example = "500")
      @RequestParam @Positive(message = "Max price must be positive") Long maxPrice,

      @Parameter(description = "Минимальная вместимость рабочего места", example = "2")
      @RequestParam @Min(value = 0, message = "Min capacity cannot be negative") Long minCapacity,

      @Parameter(hidden = true) Pageable pageable
  ) {
    return bookingService.getUserBookingsNative(maxPrice, minCapacity, pageable);
  }

  @Operation(
      summary = "Получить список всех бронирований",
      description = "Возвращает все бронирования с пагинацией и сортировкой"
  )
  @GetMapping
  public Page<BookingDto> getAllBookings(
      @Parameter(hidden = true) Pageable pageable
  ) {
    return bookingService.getAllBookings(pageable);
  }

  @Operation(
      summary = "Получить бронирование по ID",
      description = "Возвращает детальную информацию о бронировании"
  )
  @GetMapping("/{id}")
  public BookingDto getBookingById(
      @Parameter(description = "ID бронирования", example = "1")
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long id
  ) {
    return bookingService.getBookingById(id);
  }

  @Operation(
      summary = "Создать бронирование",
      description = "Создаёт новое бронирование рабочего места на указанный период. "
          + "Если время занято, вернётся ошибка"
  )
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookingDto createBooking(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Данные для создания бронирования",
          required = true
      )
      @Valid @RequestBody BookingCreateDto createDto
  ) {
    return bookingService.createBooking(createDto);
  }

  @Operation(
      summary = "Обновить бронирование",
      description = "Обновляет бронирование. Нельзя обновить завершённое или отменённое"
  )
  @PutMapping("/{id}")
  public BookingDto updateBooking(
      @Parameter(description = "ID бронирования", example = "1")
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long id,

      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Новые данные бронирования",
          required = true
      )
      @Valid @RequestBody BookingUpdateDto updateDto
  ) {
    return bookingService.updateBooking(id, updateDto);
  }

  @Operation(
      summary = "Отменить бронирование",
      description = "Отменяет бронирование. Нельзя отменить уже завершённое или отменённое"
  )
  @PostMapping("/{id}/cancel")
  public BookingDto cancelBooking(
      @Parameter(description = "ID бронирования", example = "1")
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long id
  ) {
    return bookingService.cancelBooking(id);
  }

  @Operation(
      summary = "Подтвердить бронирование",
      description = "Подтверждает бронирование. Бронирование должно быть в статусе PENDING"
  )
  @PostMapping("/{id}/confirm")
  public BookingDto confirmBooking(
      @Parameter(description = "ID бронирования", example = "1")
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long id
  ) {
    return bookingService.confirmBooking(id);
  }

  @Operation(
      summary = "Удалить бронирование",
      description = "Удаляет бронирование из системы"
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBooking(
      @Parameter(description = "ID бронирования", example = "1")
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long id
  ) {
    bookingService.deleteBooking(id);
  }

  @Operation(
      summary = "Получить бронирования рабочего места",
      description = "Возвращает все бронирования для указанного рабочего места"
  )
  @GetMapping("/workspace/{workspaceId}")
  public List<BookingDto> getWorkspaceBookings(
      @Parameter(description = "ID рабочего места", example = "1")
      @PathVariable @Positive(message = "Workspace ID must be a positive number") Long workspaceId
  ) {
    return bookingService.getWorkspaceBookings(workspaceId);
  }

  @Operation(
      summary = "Получить активные бронирования пользователя",
      description = "Возвращает бронирования пользователя в статусе PENDING"
  )
  @GetMapping("/user/{userId}/active")
  public List<BookingDto> getUserActiveBookings(
      @Parameter(description = "ID пользователя", example = "1")
      @PathVariable @Positive(message = "User ID must be a positive number") Long userId
  ) {
    return bookingService.getUserActiveBookings(userId);
  }
}