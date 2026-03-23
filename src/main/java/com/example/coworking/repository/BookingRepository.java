package com.example.coworking.repository;

import com.example.coworking.model.Booking;
import com.example.coworking.model.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  List<Booking> findByUserId(Long userId);

  List<Booking> findByWorkspaceId(Long workspaceId);

  List<Booking> findByStatus(BookingStatus status);

  List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

  @Query("SELECT b FROM Booking b WHERE b.workspace.id = :workspaceId "
      + "AND b.status != 'CANCELLED' "
      + "AND ((b.startTime <= :endTime AND b.endTime >= :startTime))")
  List<Booking> findConflictingBookings(@Param("workspaceId") Long workspaceId,
      @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

  List<Booking> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

  @Query("SELECT b FROM Booking b WHERE b.user.id = :userId "
      + "AND b.startTime > :now AND b.status != 'CANCELLED' " + "ORDER BY b.startTime ASC")
  List<Booking> findUpcomingBookings(@Param("userId") Long userId, @Param("now") LocalDateTime now);

  @Query("SELECT b FROM Booking b WHERE b.user.id = :userId "
      + "AND b.endTime < :now ORDER BY b.endTime DESC")
  List<Booking> findPastBookings(@Param("userId") Long userId, @Param("now") LocalDateTime now);

}