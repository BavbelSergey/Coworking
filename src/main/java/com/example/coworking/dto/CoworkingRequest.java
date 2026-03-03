package com.example.coworking.dto;

import java.util.Set;
import lombok.Data;

@Data
public class CoworkingRequest {

  private String name;
  private long id;
  private int size;
  private Double price;
  private String address;
  private String phoneNumber;
  private Set<String> tags;
}
