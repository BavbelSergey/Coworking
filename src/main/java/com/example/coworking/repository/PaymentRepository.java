package com.example.coworking.repository;

import com.example.coworking.model.Payment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

  @EntityGraph(attributePaths = {"booking", "booking.user", "booking.workspace"})
  Optional<Payment> findByBookingId(Long bookingId);

  List<Payment> findByDateBetween(LocalDateTime start, LocalDateTime end);

  List<Payment> findByPaymentMethod(String paymentMethod);

  @EntityGraph(attributePaths = {"booking", "booking.user", "booking.workspace"})
  @Query("SELECT p FROM Payment p WHERE p.booking.user.id = :userId")
  List<Payment> findByUserId(@Param("userId") Long userId);

  @EntityGraph(attributePaths = {"booking", "booking.user", "booking.workspace"})
  @Query("SELECT p FROM Payment p WHERE p.booking.workspace.id = :workspaceId")
  List<Payment> findByWorkspaceId(@Param("workspaceId") Long workspaceId);

  @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.date BETWEEN :start AND :end")
  Double getTotalAmountBetween(@Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  boolean existsByBookingId(Long bookingId);

  void deleteByBookingId(Long bookingId);
}