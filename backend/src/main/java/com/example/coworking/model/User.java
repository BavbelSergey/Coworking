package com.example.coworking.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long id;

  @ToString.Include
  private String name;

  @Column(unique = true)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String email;

  @ToString.Include
  private String phone;

  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @ToString.Include
  private UserRole role = UserRole.USER;

  @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
  private List<Booking> bookings = new ArrayList<>();

  @PrePersist
  private void assignDefaultRole() {
    if (role == null) {
      role = UserRole.USER;
    }
  }
}
