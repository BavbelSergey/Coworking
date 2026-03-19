package com.example.coworking.repository;

import com.example.coworking.model.Workspace;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoworkingRepository extends JpaRepository<Workspace, Long> {

  List<Workspace> findByName(String name);

  List<Workspace> findByNameContainingIgnoreCase(String name);

  List<Workspace> findByAddressContainingIgnoreCase(String address);

  List<Workspace> findByPriceBetween(Double minPrice, Double maxPrice);

  List<Workspace> findBySizeGreaterThanEqual(Integer size);

  Optional<Workspace> findByPhoneNumber(String phoneNumber);

  boolean existsByName(String name);

  long countBySizeGreaterThan(Integer size);

  void deleteByName(String name);
}