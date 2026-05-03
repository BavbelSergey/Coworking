package com.example.coworking.controller;

import com.example.coworking.dto.PaymentCreateDto;
import com.example.coworking.dto.PaymentDto;
import com.example.coworking.dto.PaymentUpdateDto;
import com.example.coworking.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Payments", description = "Управление платежами за бронирования")
public class PaymentController {

  private final PaymentService paymentService;

  @Operation(
      summary = "Получить список платежей",
      description = "Возвращает все платежи с пагинацией и сортировкой по дате "
          + "(по умолчанию — от новых к старым)"
  )
  @GetMapping
  public Page<PaymentDto> getAllPaymentsPaged(
      @Parameter(hidden = true)
      @PageableDefault(size = 20, sort = "date", direction = Direction.DESC) Pageable pageable
  ) {
    return paymentService.getAllPayments(pageable);
  }

  @Operation(
      summary = "Получить платёж по ID",
      description = "Возвращает детальную информацию о платеже"
  )
  @GetMapping("/{id}")
  public PaymentDto getPaymentById(
      @Parameter(description = "ID платежа", example = "1")
      @PathVariable @Positive(message = "Payment ID must be a positive number") Long id
  ) {
    return paymentService.getPaymentById(id);
  }

  @Operation(
      summary = "Получить платёж по ID бронирования",
      description = "Возвращает платёж, связанный с указанным бронированием"
  )
  @GetMapping("/booking/{bookingId}")
  public PaymentDto getPaymentByBookingId(
      @Parameter(description = "ID бронирования", example = "1")
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long bookingId
  ) {
    return paymentService.getPaymentByBookingId(bookingId);
  }

  @Operation(
      summary = "Создать платёж",
      description = "Создаёт новый платёж для бронирования. "
          + "Для каждого бронирования может быть только один платёж"
  )
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PaymentDto createPayment(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Данные для создания платежа",
          required = true
      )
      @Valid @RequestBody PaymentCreateDto createDto
  ) {
    return paymentService.createPayment(createDto);
  }

  @Operation(
      summary = "Обновить платёж",
      description = "Обновляет данные существующего платежа"
  )
  @PutMapping("/{id}")
  public PaymentDto updatePayment(
      @Parameter(description = "ID платежа", example = "1")
      @PathVariable @Positive(message = "Payment ID must be a positive number") Long id,

      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Новые данные платежа",
          required = true
      )
      @Valid @RequestBody PaymentUpdateDto updateDto
  ) {
    return paymentService.updatePayment(id, updateDto);
  }

  @Operation(
      summary = "Удалить платёж",
      description = "Удаляет платёж по ID"
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePayment(
      @Parameter(description = "ID платежа", example = "1")
      @PathVariable @Positive(message = "Payment ID must be a positive number") Long id
  ) {
    paymentService.deletePayment(id);
  }

  @Operation(
      summary = "Удалить платёж по ID бронирования",
      description = "Удаляет платёж, связанный с указанным бронированием"
  )
  @DeleteMapping("/booking/{bookingId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePaymentByBookingId(
      @Parameter(description = "ID бронирования", example = "1")
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long bookingId
  ) {
    paymentService.deletePaymentByBookingId(bookingId);
  }

  @Operation(
      summary = "Получить платежи пользователя",
      description = "Возвращает все платежи указанного пользователя"
  )
  @GetMapping("/user/{userId}")
  public List<PaymentDto> getUserPayments(
      @Parameter(description = "ID пользователя", example = "1")
      @PathVariable @Positive(message = "User ID must be a positive number") Long userId
  ) {
    return paymentService.getUserPayments(userId);
  }

  @Operation(
      summary = "Получить платежи по рабочему месту",
      description = "Возвращает все платежи, связанные с указанным рабочим местом"
  )
  @GetMapping("/workspace/{workspaceId}")
  public List<PaymentDto> getWorkspacePayments(
      @Parameter(description = "ID рабочего места", example = "1")
      @PathVariable @Positive(message = "Workspace ID must be a positive number") Long workspaceId
  ) {
    return paymentService.getWorkspacePayments(workspaceId);
  }

  @Operation(
      summary = "Получить платежи по методу оплаты",
      description = "Возвращает платежи, "
          + "совершённые указанным методом (например, CARD, CASH, ONLINE)"
  )
  @GetMapping("/method/{method}")
  public List<PaymentDto> getPaymentsByMethod(
      @Parameter(description = "Метод оплаты", example = "CARD")
      @PathVariable @NotBlank(message = "Payment method cannot be blank") String method
  ) {
    return paymentService.getPaymentsByMethod(method);
  }

  @Operation(
      summary = "Получить общую сумму платежей за период",
      description = "Возвращает суммарную выручку за указанный период"
  )
  @GetMapping("/total")
  public Double getTotalAmountInPeriod(
      @Parameter(description = "Начало периода (ISO формат)", example = "2026-05-01T00:00:00")
      @RequestParam @NotNull(message = "Start date is required")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,

      @Parameter(description = "Конец периода (ISO формат)", example = "2026-05-31T23:59:59")
      @RequestParam @NotNull(message = "End date is required")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
  ) {
    return paymentService.getTotalAmountInPeriod(start, end);
  }

  @Operation(
      summary = "Проверить, оплачено ли бронирование",
      description = "Возвращает true, если для указанного бронирования существует платёж"
  )
  @GetMapping("/check/booking/{bookingId}")
  public Boolean isBookingPaid(
      @Parameter(description = "ID бронирования", example = "1")
      @PathVariable @Positive(message = "Booking ID must be a positive number") Long bookingId
  ) {
    return paymentService.isBookingPaid(bookingId);
  }
}