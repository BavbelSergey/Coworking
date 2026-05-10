package com.example.coworking.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter {

  private final AtomicInteger counter = new AtomicInteger();

  public void increment() {
    counter.incrementAndGet();
  }

  public int get() {
    return counter.get();
  }
}
