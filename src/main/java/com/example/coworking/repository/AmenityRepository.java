package com.example.coworking.repository;

import com.example.coworking.model.Amenity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

  Optional<Amenity> findByName(String name);

  List<Amenity> findByNameContainingIgnoreCase(String name);

  boolean existsByName(String name);

  void deleteByName(String name);

  @Query("SELECT DISTINCT a FROM Amenity a JOIN a.workspaces w WHERE w.capacity >= :minCapacity")
  List<Amenity> findByWorkspaceMinCapacity(@Param("minCapacity") Integer minCapacity);

  @Query("SELECT DISTINCT a FROM Amenity a JOIN a.workspaces w WHERE w.pricePerHour <= :maxPrice")
  List<Amenity> findByWorkspaceMaxPrice(@Param("maxPrice") java.math.BigDecimal maxPrice);

  @Query("SELECT a.name FROM Amenity a")
  List<String> findAllNames();

  @Query("SELECT a FROM Amenity a WHERE a NOT IN "
      + "(SELECT a2 FROM Amenity a2 JOIN a2.workspaces w WHERE w.id = :workspaceId)")
  List<Amenity> findNotInWorkspace(@Param("workspaceId") Long workspaceId);

  List<Amenity> findByDescriptionContainingIgnoreCase(String description);

  @Query("SELECT a.name, COUNT(w) FROM Amenity a LEFT JOIN a.workspaces w GROUP BY a.id, a.name")
  List<Object[]> countWorkspaces();
}