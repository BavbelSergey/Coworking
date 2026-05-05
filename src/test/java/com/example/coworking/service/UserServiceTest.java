package com.example.coworking.service;

import com.example.coworking.dto.UserCreateDto;
import com.example.coworking.dto.UserDto;
import com.example.coworking.dto.UserUpdateDto;
import com.example.coworking.exception.ConflictException;
import com.example.coworking.exception.ErrorCode;
import com.example.coworking.exception.NotFoundException;
import com.example.coworking.mapper.UserMapper;
import com.example.coworking.model.Booking;
import com.example.coworking.model.BookingStatus;
import com.example.coworking.model.User;
import com.example.coworking.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService tests")
class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserService userService;

  private User user;
  private UserDto userDto;
  private UserCreateDto createDto;
  private UserUpdateDto updateDto;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setName("Иван Петров");
    user.setEmail("ivan@example.com");
    user.setPhone("+79161234567");

    userDto = new UserDto();
    userDto.setId(1L);
    userDto.setName("Иван Петров");
    userDto.setEmail("ivan@example.com");
    userDto.setPhone("+79161234567");

    createDto = new UserCreateDto();
    createDto.setName("Иван Петров");
    createDto.setEmail("ivan@example.com");
    createDto.setPhone("+79161234567");

    updateDto = new UserUpdateDto();
    updateDto.setName("Иван Сидоров");
    updateDto.setEmail("ivan_new@example.com");
    updateDto.setPhone("+79169876543");
  }

  @AfterEach
  void tearDown() {
    reset(userRepository, userMapper);
  }

  // ==================== getAllUsers ====================

  @Nested
  @DisplayName("getAllUsers")
  class GetAllUsers {

    @Test
    @DisplayName("Should return page of users")
    void shouldReturnPage() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<User> page = new PageImpl<>(List.of(user), pageable, 1);
      when(userRepository.findAll(pageable)).thenReturn(page);
      when(userMapper.toDto(any())).thenReturn(userDto);

      Page<UserDto> result = userService.getAllUsers(pageable);

      assertNotNull(result);
      assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when no users")
    void shouldReturnEmptyPage() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
      when(userRepository.findAll(pageable)).thenReturn(emptyPage);

      Page<UserDto> result = userService.getAllUsers(pageable);

      assertNotNull(result);
      assertEquals(0, result.getTotalElements());
    }
  }

  // ==================== getUserById ====================

  @Nested
  @DisplayName("getUserById")
  class GetUserById {

    @Test
    @DisplayName("Should return user when found")
    void shouldReturnUser_WhenFound() {
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userMapper.toDto(user)).thenReturn(userDto);

      UserDto result = userService.getUserById(1L);

      assertNotNull(result);
      assertEquals("ivan@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(userRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> userService.getUserById(99L));
      assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== getUserByEmail ====================

  @Nested
  @DisplayName("getUserByEmail")
  class GetUserByEmail {

    @Test
    @DisplayName("Should return user when found")
    void shouldReturnUser_WhenFound() {
      when(userRepository.findByEmail("ivan@example.com")).thenReturn(Optional.of(user));
      when(userMapper.toDto(user)).thenReturn(userDto);

      UserDto result = userService.getUserByEmail("ivan@example.com");

      assertNotNull(result);
      assertEquals("ivan@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> userService.getUserByEmail("unknown@example.com"));
      assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== createUser ====================

  @Nested
  @DisplayName("createUser")
  class CreateUser {

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUser() {
      when(userRepository.existsByEmail("ivan@example.com")).thenReturn(false);
      when(userRepository.existsByPhone("+79161234567")).thenReturn(false);
      when(userMapper.toEntity(createDto)).thenReturn(user);
      when(userRepository.save(user)).thenReturn(user);
      when(userMapper.toDto(user)).thenReturn(userDto);

      UserDto result = userService.createUser(createDto);

      assertNotNull(result);
      assertEquals("ivan@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Should create user with null phone")
    void shouldCreateUser_WithNullPhone() {
      createDto.setPhone(null);
      when(userRepository.existsByEmail("ivan@example.com")).thenReturn(false);
      when(userMapper.toEntity(createDto)).thenReturn(user);
      when(userRepository.save(user)).thenReturn(user);
      when(userMapper.toDto(user)).thenReturn(userDto);

      UserDto result = userService.createUser(createDto);

      assertNotNull(result);
      verify(userRepository, never()).existsByPhone(any());
    }

    @Test
    @DisplayName("Should throw ConflictException when email exists")
    void shouldThrowException_WhenEmailExists() {
      when(userRepository.existsByEmail("ivan@example.com")).thenReturn(true);

      ConflictException ex = assertThrows(ConflictException.class,
          () -> userService.createUser(createDto));
      assertEquals(ErrorCode.USER_EXISTS_WITH_EMAIL, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when phone exists")
    void shouldThrowException_WhenPhoneExists() {
      when(userRepository.existsByEmail("ivan@example.com")).thenReturn(false);
      when(userRepository.existsByPhone("+79161234567")).thenReturn(true);

      ConflictException ex = assertThrows(ConflictException.class,
          () -> userService.createUser(createDto));
      assertEquals(ErrorCode.USER_EXISTS_WITH_PHONE, ex.getErrorCode());
    }
  }

  // ==================== updateUser ====================

  @Nested
  @DisplayName("updateUser")
  class UpdateUser {

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUser() {
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userRepository.existsByEmail("ivan_new@example.com")).thenReturn(false);
      when(userRepository.existsByPhone("+79169876543")).thenReturn(false);
      when(userRepository.save(user)).thenReturn(user);
      when(userMapper.toDto(user)).thenReturn(userDto);

      UserDto result = userService.updateUser(1L, updateDto);

      assertNotNull(result);
      verify(userMapper).updateEntity(updateDto, user);
    }

    @Test
    @DisplayName("Should throw NotFoundException when user not found")
    void shouldThrowException_WhenNotFound() {
      when(userRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> userService.updateUser(99L, updateDto));
      assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when new email already taken")
    void shouldThrowException_WhenEmailTaken() {
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userRepository.existsByEmail("ivan_new@example.com")).thenReturn(true);

      ConflictException ex = assertThrows(ConflictException.class,
          () -> userService.updateUser(1L, updateDto));
      assertEquals(ErrorCode.USER_EXISTS_WITH_EMAIL, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when new phone already taken")
    void shouldThrowException_WhenPhoneTaken() {
      updateDto.setEmail(null); // не меняем email
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userRepository.existsByPhone("+79169876543")).thenReturn(true);

      ConflictException ex = assertThrows(ConflictException.class,
          () -> userService.updateUser(1L, updateDto));
      assertEquals(ErrorCode.USER_EXISTS_WITH_PHONE, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should allow update when email unchanged")
    void shouldAllowUpdate_WhenEmailUnchanged() {
      updateDto.setEmail("ivan@example.com");
      updateDto.setPhone(null);
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userRepository.existsByEmail("ivan@example.com")).thenReturn(true);
      when(userRepository.save(user)).thenReturn(user);
      when(userMapper.toDto(user)).thenReturn(userDto);

      UserDto result = userService.updateUser(1L, updateDto);

      assertNotNull(result);
      verify(userRepository).existsByEmail("ivan@example.com");
    }

    @Test
    @DisplayName("Should allow update when phone unchanged")
    void shouldAllowUpdate_WhenPhoneUnchanged() {
      updateDto.setEmail(null);
      updateDto.setPhone("+79161234567"); // same as user.getPhone()
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userRepository.save(user)).thenReturn(user);
      when(userMapper.toDto(user)).thenReturn(userDto);

      UserDto result = userService.updateUser(1L, updateDto);

      assertNotNull(result);
      verify(userRepository, never()).existsByPhone(any());
    }

    @Test
    @DisplayName("Should allow update when email and phone are null")
    void shouldAllowUpdate_WhenEmailAndPhoneNull() {
      updateDto.setEmail(null);
      updateDto.setPhone(null);
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userRepository.save(user)).thenReturn(user);
      when(userMapper.toDto(user)).thenReturn(userDto);

      UserDto result = userService.updateUser(1L, updateDto);

      assertNotNull(result);
      verify(userRepository, never()).existsByEmail(any());
      verify(userRepository, never()).existsByPhone(any());
    }
  }

  // ==================== deleteUser ====================

  @Nested
  @DisplayName("deleteUser")
  class DeleteUser {

    @Test
    @DisplayName("Should delete user when bookings is null")
    void shouldDeleteUser_WhenBookingsNull() {
      user.setBookings(null);
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));

      userService.deleteUser(1L);

      verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUser() {
      user.setBookings(Collections.emptyList());
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));

      userService.deleteUser(1L);

      verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(userRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> userService.deleteUser(99L));
      assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when user has PENDING bookings")
    void shouldThrowException_WhenHasActiveBookings() {
      Booking pendingBooking = new Booking();
      pendingBooking.setStatus(BookingStatus.PENDING);
      user.setBookings(List.of(pendingBooking));
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));

      ConflictException ex = assertThrows(ConflictException.class,
          () -> userService.deleteUser(1L));
      assertEquals(ErrorCode.USER_HAS_ACTIVE_BOOKINGS, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should delete user when bookings are not PENDING")
    void shouldDeleteUser_WhenBookingsNotPending() {
      Booking confirmedBooking = new Booking();
      confirmedBooking.setStatus(BookingStatus.CONFIRMED);
      Booking cancelledBooking = new Booking();
      cancelledBooking.setStatus(BookingStatus.CANCELLED);
      user.setBookings(List.of(confirmedBooking, cancelledBooking));
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));

      userService.deleteUser(1L);

      verify(userRepository).delete(user);
    }
  }

  // ==================== searchUsersByName ====================

  @Nested
  @DisplayName("searchUsersByName")
  class SearchUsersByName {

    @Test
    @DisplayName("Should return matching users")
    void shouldReturnMatchingUsers() {
      when(userRepository.findByNameContainingIgnoreCase("Иван"))
          .thenReturn(List.of(user));
      when(userMapper.toDtoList(any())).thenReturn(List.of(userDto));

      List<UserDto> result = userService.searchUsersByName("Иван");

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no matches")
    void shouldReturnEmptyList() {
      when(userRepository.findByNameContainingIgnoreCase("Nonexistent"))
          .thenReturn(Collections.emptyList());
      when(userMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<UserDto> result = userService.searchUsersByName("Nonexistent");

      assertTrue(result.isEmpty());
    }
  }

  // ==================== getUsersWithActiveBookings ====================

  @Nested
  @DisplayName("getUsersWithActiveBookings")
  class GetUsersWithActiveBookings {

    @Test
    @DisplayName("Should return users with active bookings")
    void shouldReturnUsers() {
      when(userRepository.findUsersWithActiveBookings()).thenReturn(List.of(user));
      when(userMapper.toDtoList(any())).thenReturn(List.of(userDto));

      List<UserDto> result = userService.getUsersWithActiveBookings();

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no users")
    void shouldReturnEmptyList() {
      when(userRepository.findUsersWithActiveBookings()).thenReturn(Collections.emptyList());
      when(userMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<UserDto> result = userService.getUsersWithActiveBookings();

      assertTrue(result.isEmpty());
    }
  }

  // ==================== getUsersWithoutBookings ====================

  @Nested
  @DisplayName("getUsersWithoutBookings")
  class GetUsersWithoutBookings {

    @Test
    @DisplayName("Should return users without bookings")
    void shouldReturnUsers() {
      when(userRepository.findUsersWithoutBookings()).thenReturn(List.of(user));
      when(userMapper.toDtoList(any())).thenReturn(List.of(userDto));

      List<UserDto> result = userService.getUsersWithoutBookings();

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when all users have bookings")
    void shouldReturnEmptyList() {
      when(userRepository.findUsersWithoutBookings()).thenReturn(Collections.emptyList());
      when(userMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<UserDto> result = userService.getUsersWithoutBookings();

      assertTrue(result.isEmpty());
    }
  }

  // ==================== existsByEmail ====================

  @Nested
  @DisplayName("existsByEmail")
  class ExistsByEmail {

    @Test
    @DisplayName("Should return true when email exists")
    void shouldReturnTrue() {
      when(userRepository.existsByEmail("ivan@example.com")).thenReturn(true);

      boolean result = userService.existsByEmail("ivan@example.com");

      assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void shouldReturnFalse() {
      when(userRepository.existsByEmail("unknown@example.com")).thenReturn(false);

      boolean result = userService.existsByEmail("unknown@example.com");

      assertFalse(result);
    }
  }
}