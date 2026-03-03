package com.example.coworking.repository;

import com.example.coworking.model.Coworking;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class CoworkingRepository {

  private final Map<Long, Coworking> storage = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  private Coworking createCoworking(String name, int size, Double price, String address,
      String phoneNumber, String... tags) {
    Coworking coworking = new Coworking();
    coworking.setName(name);
    coworking.setSize(size);
    coworking.setPrice(price);
    coworking.setAddress(address);
    coworking.setPhoneNumber(phoneNumber);
    coworking.setTags(new HashSet<>(Arrays.asList(tags)));
    return coworking;
  }

  public CoworkingRepository() {
    save(createCoworking("WorkSpace Premium", 50, 1500.0, "ул. Ленина, 15, Москва",
        "+7 (495) 123-45-67", "wifi", "кофе", "переговорки", "parking"));

    save(createCoworking("Smart Office", 25, 800.0, "пр. Мира, 42, Санкт-Петербург",
        "+7 (812) 234-56-78", "wifi", "тишина", "коворкинг", "24/7"));

    save(createCoworking("IT Hub", 100, 2000.0, "ул. Тверская, 7, Москва",
        "+7 (495) 345-67-89", "wifi", "IT", "мероприятия", "терраса"));

    save(createCoworking("Green Desk", 30, 900.0, "наб. реки Фонтанки, 12, Санкт-Петербург",
        "+7 (812) 456-78-90", "wifi", "растения", "лофт", "тишина"));

    save(createCoworking("Startup Station", 75, 1200.0, "ул. Баумана, 23, Казань",
        "+7 (843) 567-89-01", "wifi", "startup", "нетворкинг", "кафе"));
  }

  public Coworking save(Coworking coworking) {
    if (coworking.getId() == 0) {
      coworking.setId(idGenerator.getAndIncrement());
    }
    storage.put(coworking.getId(), coworking);
    return coworking;
  }

  public Coworking findById(Long id) {
    return (storage.get(id));
  }

  public List<Coworking> findAll() {
    return new ArrayList<>(storage.values());
  }

  public void deleteById(Long id) {
    storage.remove(id);
  }

  public List<Coworking> findByPrice(Double price) {
    return storage.values().stream()
        .filter(coworking -> coworking.getPrice() <= price)
        .toList();
  }
}
