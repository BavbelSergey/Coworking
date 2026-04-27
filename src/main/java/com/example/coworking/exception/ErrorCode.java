package com.example.coworking.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  USER_NOT_FOUND("User not found", "USER_NOT_FOUND"),
  USER_EXISTS_WITH_PHONE("User exists with this phone number", "USER_EXISTS"),
  USER_HAS_ACTIVE_BOOKINGS("Can not delete user with active bookings", "USER_HAS_ACTIVE_BOOKINGS"),
  USER_EXISTS_WITH_EMAIL("User exists with this email address", "USER_EXISTS"),
  WORKSPACE_NOT_FOUND("Workspace not found", "WORKSPACE_NOT_FOUND"),
  WORKSPACE_EXISTS_WITH_NUMBER("User exists with this phone number", "USER_EXISTS"),
  WORKSPACE_HAS_ACTIVE_BOOKINGS(
      "Can not delete workspace with active bookings", "WORKSPACE_HAS_ACTIVE_BOOKINGS"),
  PAYMENT_EXISTS_FOR_BOOKING(
      "Payment for this booking already exists", "PAYMENT_EXISTS_FOR_BOOKING"),
  PAYMENT_NOT_FOUND("Payment not found", "PAYMENT_NOT_FOUND"),
  BOOKING_NOT_FOUND("Booking not found", "BOOKING_NOT_FOUND"),
  AMENITY_NOT_FOUND("Amenity not found", "AMENITY_NOT_FOUND");

  private final String message;
  private final String code;

  ErrorCode(String message, String code) {
    this.message = message;
    this.code = code;
  }
}
