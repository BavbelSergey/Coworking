package com.example.coworking.repository;

import com.example.coworking.model.Workspace;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

  @Override
  @NonNull
  @EntityGraph(attributePaths = {"amenities"})
  Page<Workspace> findAll(@org.jspecify.annotations.NonNull Pageable pageable);

  @Query("SELECT DISTINCT w FROM Workspace w " + "JOIN w.amenities a "
      + "WHERE (:#{#minCapacity == null} = true OR w.capacity >= :minCapacity) "
      + "AND (:#{#maxPrice == null} = true OR w.pricePerHour <= :maxPrice) "
      + "AND a.id IN :amenityIds " + "GROUP BY w.id, w.number, w.capacity, w.pricePerHour "
      + "HAVING COUNT(DISTINCT a.id) = :#{#amenityIds.size()} " + "ORDER BY w.pricePerHour ASC")
  Page<Workspace> findByAmenitiesAndFilters(@Param("minCapacity") Integer minCapacity,
      @Param("maxPrice") BigDecimal maxPrice, @Param("amenityIds") List<Long> amenityIds,
      Pageable pageable);

  boolean existsByNumber(Integer number);

  @EntityGraph(attributePaths = {"amenities"})
  Optional<Workspace> findByNumber(Integer number);

  void deleteByNumber(Integer number);

  @EntityGraph(attributePaths = {"amenities"})
  List<Workspace> findByCapacityGreaterThanEqual(Integer capacity);

  @EntityGraph(attributePaths = {"amenities"})
  List<Workspace> findByPricePerHourLessThanEqual(BigDecimal pricePerHour);

  @EntityGraph(attributePaths = {"amenities"})
  @Query("SELECT w FROM Workspace w WHERE "
      + "(:minCapacity IS NULL OR w.capacity >= :minCapacity) AND "
      + "(:maxPrice IS NULL OR w.pricePerHour <= :maxPrice)")
  List<Workspace> findAvailableWorkspaces(@Param("minCapacity") Integer minCapacity,
      @Param("maxPrice") BigDecimal maxPrice);

}