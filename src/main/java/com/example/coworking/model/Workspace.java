package com.example.coworking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coworkings")
public class Workspace {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer number;

  private Integer capacity;
  private BigDecimal pricePerHour;

  @ManyToMany
  @JoinTable(
      name = "workspace_amenity",
      joinColumns = @JoinColumn(name = "workspace_id"),
      inverseJoinColumns = @JoinColumn(name = "amenity_id")
  )
  private List<Amenity> amenities = new ArrayList<>();

  @OneToMany(mappedBy = "workspace")
  private List<Booking> bookings = new ArrayList<>();
}
