package com.example.coworking.model;

import java.util.HashSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coworking {

  private String name;
  private long id;
  private int size;
  private int price;
  private String address;
  private String phoneNumber;
  private HashSet<String> tags;
}
