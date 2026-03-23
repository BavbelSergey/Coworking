package com.example.coworking.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "workspaces")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Workspace {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long id;

  @Column(unique = true)
  @ToString.Include
  private Integer number;

  @ToString.Include
  private Integer capacity;

  @ToString.Include
  private BigDecimal pricePerHour;

  @ManyToMany
  @JoinTable(
      name = "workspace_amenities",
      joinColumns = @JoinColumn(name = "workspace_id"),
      inverseJoinColumns = @JoinColumn(name = "amenity_id")
  )
  private List<Amenity> amenities = new ArrayList<>();

  @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL)
  @BatchSize(size = 20)
  private List<Booking> bookings = new ArrayList<>();
}
