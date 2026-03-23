package com.example.coworking.mapper;

import com.example.coworking.dto.UserCreateDto;
import com.example.coworking.dto.UserDto;
import com.example.coworking.dto.UserUpdateDto;
import com.example.coworking.model.BookingStatus;
import com.example.coworking.model.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserDto toDto(User user) {
    if (user == null) {
      return null;
    }

    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setName(user.getName());
    dto.setEmail(user.getEmail());
    dto.setPhone(user.getPhone());

    if (user.getBookings() != null) {
      dto.setTotalBookings(user.getBookings().size());

      long activeCount = user.getBookings().stream()
          .filter(b -> b.getStatus() == BookingStatus.PENDING).count();
      dto.setActiveBookings((int) activeCount);
    }

    return dto;
  }

  public User toEntity(UserCreateDto createDto) {
    if (createDto == null) {
      return null;
    }

    User user = new User();
    user.setName(createDto.getName());
    user.setEmail(createDto.getEmail());
    user.setPhone(createDto.getPhone());

    return user;
  }

  public void updateEntity(UserUpdateDto updateDto, User user) {
    if (updateDto == null || user == null) {
      return;
    }

    if (updateDto.getName() != null) {
      user.setName(updateDto.getName());
    }
    if (updateDto.getEmail() != null) {
      user.setEmail(updateDto.getEmail());
    }
    if (updateDto.getPhone() != null) {
      user.setPhone(updateDto.getPhone());
    }
  }

  public List<UserDto> toDtoList(List<User> users) {
    if (users == null) {
      return new ArrayList<>();
    }
    return users.stream().map(this::toDto).toList();
  }
}