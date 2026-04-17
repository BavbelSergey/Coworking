package com.example.coworking.cache;

import com.example.coworking.dto.WorkspaceDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceSearchCache {

  private final Map<WorkspaceSearchKey, Page<WorkspaceDto>> cache = new HashMap<>();

  public Page<WorkspaceDto> get(List<Long> amenityIds,
      Pageable pageable) {
    WorkspaceSearchKey key = new WorkspaceSearchKey(amenityIds, pageable);

    return cache.get(key);
  }

  public void put(List<Long> amenityIds, Pageable pageable,
      Page<WorkspaceDto> result) {
    WorkspaceSearchKey key = new WorkspaceSearchKey(amenityIds, pageable);
    cache.put(key, result);
  }

  public void clear() {
    cache.clear();
  }

  public void evict(List<Long> amenityIds, org.springframework.data.domain.Pageable pageable) {
    WorkspaceSearchKey key = new WorkspaceSearchKey(amenityIds, pageable);
    cache.remove(key);
  }

  public int size() {
    return cache.size();
  }
}