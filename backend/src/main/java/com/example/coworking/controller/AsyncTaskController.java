package com.example.coworking.controller;

import com.example.coworking.dto.AsyncTaskResponseDto;
import com.example.coworking.service.AsyncTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tasks")
@Tag(name = "AsyncTask", description = "Запуск и отслеживание асинхронных задач")
public class AsyncTaskController {

  private final AsyncTaskService asyncTaskService;

  @Operation(summary = "Запуск асинхронной задачи", description =
      "Создает и запускает новую асинхронную задачу для текущего пользователя")
  @PostMapping
  public AsyncTaskResponseDto startTask() {
    return asyncTaskService.startTask();
  }

  @Operation(summary = "Получить статус задачи", description =
      "Возвращает текущее состояние асинхронной задачи по её ID")
  @GetMapping("/{id}")
  public AsyncTaskResponseDto getStatus(
      @Parameter(description = "Идентификатор задачи",
          example = "task-12345") @PathVariable String id) {
    return asyncTaskService.getById(id);
  }
}