package com.example.coworking.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoworkingResponse {

  private String name;
  private long id;
  private int size;
  private int price;
  private String address;
  private String phoneNumber;
  private Set<String> tags;
}
