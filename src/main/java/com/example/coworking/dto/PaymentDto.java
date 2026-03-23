package com.example.coworking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {

  private Long id;
  private BigDecimal amount;
  private LocalDateTime date;
  private String paymentMethod;

  private Long bookingId;
  private LocalDateTime bookingStartTime;
  private LocalDateTime bookingEndTime;
  private Long userId;
  private String userName;
  private Long workspaceId;
  private Integer workspaceNumber;
}