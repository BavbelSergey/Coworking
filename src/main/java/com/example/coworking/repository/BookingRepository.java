package com.example.coworking.repository;

import com.example.coworking.model.Booking;
import com.example.coworking.model.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  @Query(
      """
      SELECT DISTINCT b FROM Booking b
      LEFT JOIN FETCH b.user
      LEFT JOIN FETCH b.workspace
      LEFT JOIN FETCH b.payment
      WHERE b.workspace.pricePerHour < :price
      AND b.workspace.capacity > :capacity
      ORDER BY b.startTime DESC
      """)
  Page<Booking> findBookingsByUserId(@RequestParam Long price,
      @RequestParam Long capacity, Pageable pageable);

  @Query(value =
      """
      SELECT DISTINCT
            b.id,
            b.start_time,
            b.end_time,
            b.created_at,
            b.status,
            b.user_id,
            b.workspace_id
        FROM bookings b
        LEFT JOIN workspaces w ON b.workspace_id = w.id
        LEFT JOIN payments p ON b.id = p.booking_id
        WHERE w.price_per_hour < :price
          AND w.capacity > :capacity
        ORDER BY b.start_time DESC
      """,
      countQuery =
      """
      SELECT COUNT(DISTINCT b.id)
      FROM bookings b
      LEFT JOIN workspaces w ON b.workspace_id = w.id
      LEFT JOIN payments p ON b.id = p.booking_id
      WHERE w.price_per_hour < :price
        AND w.capacity > :capacity
      """,
      nativeQuery = true)
  Page<Booking> findBookingsByUserIdNative(@RequestParam Long price,
      @RequestParam Long capacity, Pageable pageable);

  @NonNull
  @EntityGraph(attributePaths = {"user", "workspace", "payment"})
  Page<Booking> findAll(@org.jspecify.annotations.NonNull Pageable pageable);

  @EntityGraph(attributePaths = {"user", "workspace", "payment"})
  List<Booking> findByWorkspaceId(Long workspaceId);

  @EntityGraph(attributePaths = {"user", "workspace", "payment"})
  List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

  @EntityGraph(attributePaths = {"user", "workspace"})
  @Query("SELECT b FROM Booking b WHERE b.workspace.id = :workspaceId "
      + "AND b.status != 'CANCELLED' "
      + "AND ((b.startTime <= :endTime AND b.endTime >= :startTime))")
  List<Booking> findConflictingBookings(@Param("workspaceId") Long workspaceId,
      @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

}