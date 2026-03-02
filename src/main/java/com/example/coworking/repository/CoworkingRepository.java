package com.example.coworking.repository;

import com.example.coworking.model.Coworking;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class CoworkingRepository {

  private final Map<Long, Coworking> storage = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  public Coworking save(Coworking coworking) {
    if (coworking.getId() == 0) {
      coworking.setId(idGenerator.getAndIncrement());
    }
    storage.put(coworking.getId(), coworking);
    return coworking;
  }

  public Optional<Coworking> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  public List<Coworking> findAll() {
    return new ArrayList<>(storage.values());
  }

  public void deleteById(Long id) {
    storage.remove(id);
  }

  public boolean existsById(Long id) {
    return storage.containsKey(id);
  }

}
