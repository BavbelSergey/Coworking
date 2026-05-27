package com.example.coworking.model;

public enum UserRole {
  ADMIN,
  USER;

  public String getAuthority() {
    return "ROLE_" + name();
  }
}
