package com.example.coworking.cache;

import com.example.coworking.dto.BookingDto;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookingSearchCache {

  private final Map<BookingSearchKey, Page<BookingDto>> cache = new HashMap<>();

  public Page<BookingDto> get(Long price, Long capacity, Pageable pageable) {
    BookingSearchKey key = new BookingSearchKey(price, capacity, pageable);

    return cache.get(key);
  }

  public void put(Long price, Long capacity, Pageable pageable,
      Page<BookingDto> result) {
    BookingSearchKey key = new BookingSearchKey(price, capacity, pageable);
    cache.put(key, result);
  }

  public void clear() {
    log.info("Кеш очищен");
    cache.clear();
  }

  public int size() {
    return cache.size();
  }
}