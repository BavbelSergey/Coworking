package com.example.coworking.service;

import com.example.coworking.dto.UserCreateDto;
import com.example.coworking.dto.UserDto;
import com.example.coworking.dto.UserUpdateDto;
import com.example.coworking.exception.ConflictException;
import com.example.coworking.exception.ErrorCode;
import com.example.coworking.exception.NotFoundException;
import com.example.coworking.mapper.UserMapper;
import com.example.coworking.model.User;
import com.example.coworking.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public Page<UserDto> getAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable).map(userMapper::toDto);
  }

  public UserDto getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    return userMapper.toDto(user);
  }

  public UserDto getUserByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    return userMapper.toDto(user);
  }

  @Transactional
  public UserDto createUser(UserCreateDto createDto) {
    if (userRepository.existsByEmail(createDto.getEmail())) {
      throw new ConflictException(ErrorCode.USER_EXISTS_WITH_EMAIL);
    }

    if (createDto.getPhone() != null && userRepository.existsByPhone(createDto.getPhone())) {
      throw new ConflictException(ErrorCode.USER_EXISTS_WITH_PHONE);
    }

    User user = userMapper.toEntity(createDto);
    User savedUser = userRepository.save(user);
    return userMapper.toDto(savedUser);
  }

  @Transactional
  public UserDto updateUser(Long id, UserUpdateDto updateDto) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    if (updateDto.getEmail() != null && !updateDto.getEmail().equals(user.getEmail())) {
      if (userRepository.existsByEmail(updateDto.getEmail())) {
        throw new ConflictException(ErrorCode.USER_EXISTS_WITH_EMAIL);
      }
    }

    if (updateDto.getPhone() != null && !updateDto.getPhone().equals(user.getPhone())
        && userRepository.existsByPhone(updateDto.getPhone())) {
      throw new ConflictException(ErrorCode.USER_EXISTS_WITH_PHONE);
    }

    userMapper.updateEntity(updateDto, user);
    User updatedUser = userRepository.save(user);
    return userMapper.toDto(updatedUser);
  }

  @Transactional
  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    boolean hasActiveBookings = user.getBookings().stream()
        .anyMatch(booking -> booking.getStatus().name().equals("PENDING"));

    if (hasActiveBookings) {
      throw new ConflictException(ErrorCode.USER_HAS_ACTIVE_BOOKINGS);
    }

    userRepository.delete(user);
  }

  public List<UserDto> searchUsersByName(String name) {
    return userMapper.toDtoList(userRepository.findByNameContainingIgnoreCase(name));
  }

  public List<UserDto> getUsersWithActiveBookings() {
    return userMapper.toDtoList(userRepository.findUsersWithActiveBookings());
  }

  public List<UserDto> getUsersWithoutBookings() {
    return userMapper.toDtoList(userRepository.findUsersWithoutBookings());
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }
}