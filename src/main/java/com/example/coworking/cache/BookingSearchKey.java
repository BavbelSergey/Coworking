package com.example.coworking.cache;

import java.util.List;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class WorkspaceSearchKey {

  private final List<Long> amenityIds;
  private final int page;
  private final int size;

  public WorkspaceSearchKey(List<Long> amenityIds, Pageable pageable) {
    this.amenityIds = amenityIds != null ? List.copyOf(amenityIds) : null;
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
    WorkspaceSearchKey that = (WorkspaceSearchKey) o;
    return page == that.page && size == that.size && Objects.equals(amenityIds, that.amenityIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amenityIds, page, size);
  }

  @Override
  public String toString() {
    return "WorkspaceByAmenitiesKey{" + "amenityIds=" + amenityIds + ", page=" + page + ", size="
        + size + ", sort='" + '\'' + '}';
  }

}