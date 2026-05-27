package com.example.coworking.cache;

import java.util.Objects;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class BookingSearchKey {

  private final Long price;
  private final Long capacity;
  private final int page;
  private final int size;

  public BookingSearchKey(Long price, Long capacity, Pageable pageable) {
    this.price = price;
    this.capacity = capacity;
    this.page = pageable.getPageNumber();
    this.size = pageable.getPageSize();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingSearchKey that = (BookingSearchKey) o;
    return page == that.page && size == that.size && Objects.equals(price, that.price)
        && Objects.equals(capacity, that.capacity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(price, capacity, page, size);
  }
}