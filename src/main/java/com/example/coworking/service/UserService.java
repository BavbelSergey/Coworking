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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public Page<UserDto> getAllUsers(Pageable pageable) {
    log.debug("Fetching all users with pageable: {}", pageable);
    Page<UserDto> result = userRepository.findAll(pageable).map(userMapper::toDto);
    log.debug("Found {} users", result.getTotalElements());
    return result;
  }

  public UserDto getUserById(Long id) {
    log.debug("Fetching user by id: {}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("User not found with id: {}", id);
          return new NotFoundException(ErrorCode.USER_NOT_FOUND);
        });
    log.info("Successfully fetched user: id={}, email={}", id, user.getEmail());
    return userMapper.toDto(user);
  }

  public UserDto getUserByEmail(String email) {
    log.debug("Fetching user by email: {}", email);
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          log.warn("User not found with email: {}", email);
          return new NotFoundException(ErrorCode.USER_NOT_FOUND);
        });
    log.info("Successfully fetched user by email: email={}, id={}", email, user.getId());
    return userMapper.toDto(user);
  }

  @Transactional
  public UserDto createUser(UserCreateDto createDto) {
    log.info("Creating new user: email={}, phone={}",
        createDto.getEmail(), createDto.getPhone());

    if (userRepository.existsByEmail(createDto.getEmail())) {
      log.warn("Cannot create user — email already exists: {}", createDto.getEmail());
      throw new ConflictException(ErrorCode.USER_EXISTS_WITH_EMAIL);
    }

    if (createDto.getPhone() != null && userRepository.existsByPhone(createDto.getPhone())) {
      log.warn("Cannot create user — phone already exists: {}", createDto.getPhone());
      throw new ConflictException(ErrorCode.USER_EXISTS_WITH_PHONE);
    }

    User user = userMapper.toEntity(createDto);
    user.setPassword(passwordEncoder.encode(createDto.getPassword()));
    User savedUser = userRepository.save(user);
    log.info("Successfully created user: id={}, email={}, phone={}",
        savedUser.getId(), savedUser.getEmail(), savedUser.getPhone());
    return userMapper.toDto(savedUser);
  }

  @Transactional
  public UserDto updateUser(Long id, UserUpdateDto updateDto) {
    log.info("Updating user: id={}, updateData={}", id, updateDto);

    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Cannot update — user not found: id={}", id);
          return new NotFoundException(ErrorCode.USER_NOT_FOUND);
        });

    if (updateDto.getEmail() != null && userRepository.existsByEmail(updateDto.getEmail())
        && !updateDto.getEmail().equals(user.getEmail())) {
      log.warn("Cannot update user id={} — email already taken: {}", id, updateDto.getEmail());
      throw new ConflictException(ErrorCode.USER_EXISTS_WITH_EMAIL);
    }

    if (updateDto.getPhone() != null && !updateDto.getPhone().equals(user.getPhone())
        && userRepository.existsByPhone(updateDto.getPhone())) {
      log.warn("Cannot update user id={} — phone already taken: {}", id, updateDto.getPhone());
      throw new ConflictException(ErrorCode.USER_EXISTS_WITH_PHONE);
    }

    String oldEmail = user.getEmail();
    String oldPhone = user.getPhone();
    userMapper.updateEntity(updateDto, user);
    User updatedUser = userRepository.save(user);
    log.info("Successfully updated user: id={}, oldEmail={}, newEmail={}, oldPhone={}, newPhone={}",
        id, oldEmail, updatedUser.getEmail(), oldPhone, updatedUser.getPhone());
    return userMapper.toDto(updatedUser);
  }

  @Transactional
  public void deleteUser(Long id) {
    log.info("Attempting to delete user: id={}", id);

    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Cannot delete — user not found: id={}", id);
          return new NotFoundException(ErrorCode.USER_NOT_FOUND);
        });

    boolean hasActiveBookings = user.getBookings() != null && user.getBookings().stream()
        .anyMatch(booking -> booking.getStatus().name().equals("PENDING"));

    if (hasActiveBookings) {
      log.warn("Cannot delete user id={} — has active (PENDING) bookings", id);
      throw new ConflictException(ErrorCode.USER_HAS_ACTIVE_BOOKINGS);
    }

    userRepository.delete(user);
    log.info("Successfully deleted user: id={}, email={}", id, user.getEmail());
  }

  public List<UserDto> searchUsersByName(String name) {
    log.debug("Searching users by name containing: {}", name);
    List<UserDto> result = userMapper.toDtoList(
        userRepository.findByNameContainingIgnoreCase(name));
    log.debug("Found {} users matching name '{}'", result.size(), name);
    return result;
  }

  public List<UserDto> getUsersWithActiveBookings() {
    log.debug("Fetching users with active bookings");
    List<UserDto> result = userMapper.toDtoList(
        userRepository.findUsersWithActiveBookings());
    log.debug("Found {} users with active bookings", result.size());
    return result;
  }

  public List<UserDto> getUsersWithoutBookings() {
    log.debug("Fetching users without bookings");
    List<UserDto> result = userMapper.toDtoList(
        userRepository.findUsersWithoutBookings());
    log.debug("Found {} users without bookings", result.size());
    return result;
  }

  public boolean existsByEmail(String email) {
    log.debug("Checking if user exists by email: {}", email);
    boolean exists = userRepository.existsByEmail(email);
    log.debug("User with email '{}' exists: {}", email, exists);
    return exists;
  }
}
