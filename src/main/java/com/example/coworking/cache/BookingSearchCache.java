package com.example.coworking.cache;

import com.example.coworking.dto.BookingDto;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class BookingSearchCache {

  private final Map<BookingSearchKey, Page<BookingDto>> cache = new HashMap<>();

  public Page<BookingDto> get(Long userId,
      Pageable pageable) {
    BookingSearchKey key = new BookingSearchKey(userId, pageable);

    return cache.get(key);
  }

  public void put(Long userId, Pageable pageable,
      Page<BookingDto> result) {
    BookingSearchKey key = new BookingSearchKey(userId, pageable);
    cache.put(key, result);
  }

  public void clear() {
    cache.clear();
  }

  public int size() {
    return cache.size();
  }
}