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

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  @Query(
      """
      SELECT DISTINCT b FROM Booking b
      LEFT JOIN FETCH b.user
      LEFT JOIN FETCH b.workspace w
      LEFT JOIN FETCH b.payment
      WHERE b.user.id = :userId
      ORDER BY b.startTime DESC
      """)
  Page<Booking> findBookingsByUserId(@Param("userId") Long userId, Pageable pageable);

  @Query(value =
      """
      SELECT DISTINCT
            b.id,
            b.start_time,
            b.end_time,
            b.created_at,
            b.status,
            u.id as user_id,
            u.name as user_name,
            u.email as user_email,
            u.phone as user_phone,
            w.id as workspace_id,
            w.number as workspace_number,
            w.capacity as workspace_capacity,
            w.price_per_hour as workspace_price,
            p.id as payment_id,
            p.amount as payment_amount,
            p.date as payment_date,
            p.payment_method as payment_method
        FROM bookings b
        LEFT JOIN users u ON b.user_id = u.id
        LEFT JOIN workspaces w ON b.workspace_id = w.id
        LEFT JOIN payments p ON b.id = p.booking_id
        WHERE b.user_id = :userId
        ORDER BY b.start_time DESC
      """, countQuery =
      """
      SELECT COUNT(*) FROM bookings b
      WHERE b.user_id = :userId
      """, nativeQuery = true)
  Page<Booking> findBookingsByUserIdNative(@Param("userId") Long userId, Pageable pageable);

  @NonNull
  @EntityGraph(attributePaths = {"user", "workspace", "payment"})
  Page<Booking> findAll(@org.jspecify.annotations.NonNull Pageable pageable);

  @EntityGraph(attributePaths = {"user", "workspace", "payment"})
  List<Booking> findByWorkspaceId(Long workspaceId);

  @EntityGraph(attributePaths = {"user", "workspace", "payment"})
  List<Booking> findByStatus(BookingStatus status);

  @EntityGraph(attributePaths = {"user", "workspace", "payment"})
  List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

  @EntityGraph(attributePaths = {"user", "workspace"})
  @Query("SELECT b FROM Booking b WHERE b.workspace.id = :workspaceId "
      + "AND b.status != 'CANCELLED' "
      + "AND ((b.startTime <= :endTime AND b.endTime >= :startTime))")
  List<Booking> findConflictingBookings(@Param("workspaceId") Long workspaceId,
      @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

  @EntityGraph(attributePaths = {"user", "workspace"})
  List<Booking> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

  @EntityGraph(attributePaths = {"user", "workspace"})
  @Query("SELECT b FROM Booking b WHERE b.user.id = :userId "
      + "AND b.startTime > :now AND b.status != 'CANCELLED' " + "ORDER BY b.startTime ASC")
  List<Booking> findUpcomingBookings(@Param("userId") Long userId, @Param("now") LocalDateTime now);

  @EntityGraph(attributePaths = {"user", "workspace"})
  @Query("SELECT b FROM Booking b WHERE b.user.id = :userId "
      + "AND b.endTime < :now ORDER BY b.endTime DESC")
  List<Booking> findPastBookings(@Param("userId") Long userId, @Param("now") LocalDateTime now);

}