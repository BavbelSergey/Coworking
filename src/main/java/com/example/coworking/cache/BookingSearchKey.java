package com.example.coworking.cache;

import java.util.Objects;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class BookingSearchKey {

  private final Long userId;
  private final int page;
  private final int size;

  public BookingSearchKey(Long userId, Pageable pageable) {
    this.userId = userId;
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
    return page == that.page && size == that.size && this.userId.equals(that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, page, size);
  }
}