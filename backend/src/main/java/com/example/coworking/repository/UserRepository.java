package com.example.coworking.repository;

import com.example.coworking.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  List<User> findByNameContainingIgnoreCase(String name);

  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);

  @EntityGraph(attributePaths = {"bookings"})
  @Query("SELECT DISTINCT u FROM User u JOIN u.bookings b WHERE b.status = 'ACTIVE'")
  List<User> findUsersWithActiveBookings();

  @EntityGraph(attributePaths = {"bookings"})
  @Query("SELECT u FROM User u WHERE SIZE(u.bookings) = 0")
  List<User> findUsersWithoutBookings();

}