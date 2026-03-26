package com.example.coworking.dto;

import com.example.coworking.model.BookingStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

  private Long id;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private LocalDateTime createdAt;
  private BookingStatus status;

  private Long userId;
  private Long workspaceId;
  private Long paymentId;
  private String userName;
  private String userEmail;
  private Integer workspaceNumber;
}