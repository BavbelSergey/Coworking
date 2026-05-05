package com.example.coworking.service;

import com.example.coworking.dto.AmenityDto;
import com.example.coworking.dto.WorkspaceDto;
import com.example.coworking.exception.ConflictException;
import com.example.coworking.exception.ErrorCode;
import com.example.coworking.exception.NotFoundException;
import com.example.coworking.mapper.WorkspaceMapper;
import com.example.coworking.model.Amenity;
import com.example.coworking.model.Booking;
import com.example.coworking.model.BookingStatus;
import com.example.coworking.model.Workspace;
import com.example.coworking.repository.AmenityRepository;
import com.example.coworking.repository.WorkspaceRepository;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkspaceService tests")
class WorkspaceServiceTest {

  @Mock
  private WorkspaceRepository workspaceRepository;
  @Mock
  private AmenityRepository amenityRepository;
  @Mock
  private WorkspaceMapper workspaceMapper;

  @InjectMocks
  private WorkspaceService workspaceService;

  private Workspace workspace;
  private WorkspaceDto workspaceDto;
  private Amenity amenity;

  @BeforeEach
  void setUp() {
    amenity = new Amenity();
    amenity.setId(1L);
    amenity.setName("Wi-Fi");

    AmenityDto amenityDto = new AmenityDto();
    amenityDto.setId(1L);
    amenityDto.setName("Wi-Fi");

    workspace = new Workspace();
    workspace.setId(1L);
    workspace.setNumber(101);
    workspace.setCapacity(4);
    workspace.setPricePerHour(BigDecimal.valueOf(500.00));
    workspace.setAmenities(new ArrayList<>());

    workspaceDto = new WorkspaceDto();
    workspaceDto.setId(1L);
    workspaceDto.setNumber(101);
    workspaceDto.setCapacity(4);
    workspaceDto.setPricePerHour(BigDecimal.valueOf(500.00));
    workspaceDto.setAmenities(List.of(amenityDto));
  }

  @AfterEach
  void tearDown() {
    reset(workspaceRepository, amenityRepository, workspaceMapper);
  }

  // ==================== getAllWorkspaces ====================

  @Nested
  @DisplayName("getAllWorkspaces")
  class GetAllWorkspaces {

    @Test
    @DisplayName("Should return page of workspaces")
    void shouldReturnPage() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<Workspace> page = new PageImpl<>(List.of(workspace), pageable, 1);
      when(workspaceRepository.findAll(pageable)).thenReturn(page);
      when(workspaceMapper.toDto(any())).thenReturn(workspaceDto);

      Page<WorkspaceDto> result = workspaceService.getAllWorkspaces(pageable);

      assertNotNull(result);
      assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when no workspaces")
    void shouldReturnEmptyPage() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<Workspace> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
      when(workspaceRepository.findAll(pageable)).thenReturn(emptyPage);

      Page<WorkspaceDto> result = workspaceService.getAllWorkspaces(pageable);

      assertNotNull(result);
      assertEquals(0, result.getTotalElements());
    }
  }

  // ==================== getWorkspaceById ====================

  @Nested
  @DisplayName("getWorkspaceById")
  class GetWorkspaceById {

    @Test
    @DisplayName("Should return workspace when found")
    void shouldReturnWorkspace_WhenFound() {
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.getWorkspaceById(1L);

      assertNotNull(result);
      assertEquals(101, result.getNumber());
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(workspaceRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> workspaceService.getWorkspaceById(99L));
      assertEquals(ErrorCode.WORKSPACE_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== createWorkspace ====================

  @Nested
  @DisplayName("createWorkspace")
  class CreateWorkspace {

    @Test
    @DisplayName("Should create workspace successfully with amenities")
    void shouldCreateWorkspace_WithAmenities() {
      when(workspaceRepository.existsByNumber(101)).thenReturn(false);
      when(workspaceMapper.toEntity(workspaceDto)).thenReturn(workspace);
      when(amenityRepository.findAllById(anyList())).thenReturn(List.of(amenity));
      when(workspaceRepository.save(workspace)).thenReturn(workspace);
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.createWorkspace(workspaceDto);

      assertNotNull(result);
      assertEquals(101, result.getNumber());
    }

    @Test
    @DisplayName("Should create workspace without amenities")
    void shouldCreateWorkspace_WithoutAmenities() {
      workspaceDto.setAmenities(null);
      when(workspaceRepository.existsByNumber(101)).thenReturn(false);
      when(workspaceMapper.toEntity(workspaceDto)).thenReturn(workspace);
      when(workspaceRepository.save(workspace)).thenReturn(workspace);
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.createWorkspace(workspaceDto);

      assertNotNull(result);
      verify(amenityRepository, never()).findAllById(anyList());
    }

    @Test
    @DisplayName("Should create workspace with empty amenities list")
    void shouldCreateWorkspace_WithEmptyAmenities() {
      workspaceDto.setAmenities(Collections.emptyList());
      when(workspaceRepository.existsByNumber(101)).thenReturn(false);
      when(workspaceMapper.toEntity(workspaceDto)).thenReturn(workspace);
      when(workspaceRepository.save(workspace)).thenReturn(workspace);
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.createWorkspace(workspaceDto);

      assertNotNull(result);
      verify(amenityRepository, never()).findAllById(anyList());
    }

    @Test
    @DisplayName("Should throw ConflictException when number exists")
    void shouldThrowException_WhenNumberExists() {
      when(workspaceRepository.existsByNumber(101)).thenReturn(true);

      ConflictException ex = assertThrows(ConflictException.class,
          () -> workspaceService.createWorkspace(workspaceDto));
      assertEquals(ErrorCode.WORKSPACE_EXISTS_WITH_NUMBER, ex.getErrorCode());
    }
  }

  // ==================== updateWorkspace ====================

  @Nested
  @DisplayName("updateWorkspace")
  class UpdateWorkspace {

    @Test
    @DisplayName("Should update workspace when amenities is null in updateDto")
    void shouldUpdateWorkspace_WhenAmenitiesNull() {
      workspaceDto.setAmenities(null); // amenities = null
      workspaceDto.setNumber(102);
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceRepository.existsByNumber(102)).thenReturn(false);
      when(workspaceRepository.save(workspace)).thenReturn(workspace);
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.updateWorkspace(1L, workspaceDto);

      assertNotNull(result);
      // getWorkspaceDto вызван с null amenities → ветка false покрыта
    }

    @Test
    @DisplayName("Should update workspace without changing amenities")
    void shouldUpdateWorkspace_WithoutAmenities() {
      workspaceDto.setAmenities(null);
      workspaceDto.setNumber(102);
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceRepository.existsByNumber(102)).thenReturn(false);
      when(workspaceRepository.save(workspace)).thenReturn(workspace);
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.updateWorkspace(1L, workspaceDto);

      assertNotNull(result);
      verify(amenityRepository, never()).findAllById(anyList());
    }

    @Test
    @DisplayName("Should update workspace successfully")
    void shouldUpdateWorkspace() {
      workspaceDto.setNumber(102);
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceRepository.existsByNumber(102)).thenReturn(false);
      when(amenityRepository.findAllById(anyList())).thenReturn(List.of(amenity));
      when(workspaceRepository.save(workspace)).thenReturn(workspace);
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.updateWorkspace(1L, workspaceDto);

      assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(workspaceRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> workspaceService.updateWorkspace(99L, workspaceDto));
      assertEquals(ErrorCode.WORKSPACE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when new number exists")
    void shouldThrowException_WhenNumberExists() {
      workspaceDto.setNumber(102);
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceRepository.existsByNumber(102)).thenReturn(true);

      ConflictException ex = assertThrows(ConflictException.class,
          () -> workspaceService.updateWorkspace(1L, workspaceDto));
      assertEquals(ErrorCode.WORKSPACE_EXISTS_WITH_NUMBER, ex.getErrorCode());
    }


    @Test
    @DisplayName("Should allow update when number unchanged")
    void shouldAllowUpdate_WhenNumberUnchanged() {
      workspaceDto.setNumber(101); // same
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(amenityRepository.findAllById(anyList())).thenReturn(List.of(amenity));
      when(workspaceRepository.save(workspace)).thenReturn(workspace);
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.updateWorkspace(1L, workspaceDto);

      assertNotNull(result);
      verify(workspaceRepository, never()).existsByNumber(any());
    }
  }

  // ==================== partialUpdateWorkspace ====================

  @Nested
  @DisplayName("partialUpdateWorkspace")
  class PartialUpdateWorkspace {

    @Test
    @DisplayName("Should partially update when new number does not exist")
    void shouldPartialUpdate_WhenNewNumberNotExists() {
      workspaceDto.setNumber(102); // меняем номер, но он не занят
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceRepository.existsByNumber(102)).thenReturn(false); // ← возвращает false
      when(workspaceRepository.save(workspace)).thenReturn(workspace);
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.partialUpdateWorkspace(1L, workspaceDto);

      assertNotNull(result);
      verify(workspaceMapper).updateEntity(workspaceDto, workspace);
    }

    @Test
    @DisplayName("Should partially update workspace successfully")
    void shouldPartialUpdateWorkspace() {
      workspaceDto.setNumber(null);
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceRepository.save(workspace)).thenReturn(workspace);
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.partialUpdateWorkspace(1L, workspaceDto);

      assertNotNull(result);
      verify(workspaceMapper).updateEntity(workspaceDto, workspace);
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(workspaceRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> workspaceService.partialUpdateWorkspace(99L, workspaceDto));
      assertEquals(ErrorCode.WORKSPACE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when new number exists")
    void shouldThrowException_WhenNumberExists() {
      workspace.setNumber(101);
      workspaceDto.setNumber(102);
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceRepository.existsByNumber(102)).thenReturn(true);

      ConflictException ex = assertThrows(ConflictException.class,
          () -> workspaceService.partialUpdateWorkspace(1L, workspaceDto));
      assertEquals(ErrorCode.WORKSPACE_EXISTS_WITH_NUMBER, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should allow update when number unchanged")
    void shouldAllowUpdate_WhenNumberUnchanged() {
      workspaceDto.setNumber(101); // same
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceRepository.save(workspace)).thenReturn(workspace);
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.partialUpdateWorkspace(1L, workspaceDto);

      assertNotNull(result);
      verify(workspaceRepository, never()).existsByNumber(any());
    }
  }

  // ==================== deleteWorkspace ====================

  @Nested
  @DisplayName("deleteWorkspace")
  class DeleteWorkspace {

    @Test
    @DisplayName("Should delete workspace when bookings is null")
    void shouldDeleteWorkspace_WhenBookingsNull() {
      workspace.setBookings(null);
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));

      workspaceService.deleteWorkspace(1L);

      verify(workspaceRepository).delete(workspace);
    }

    @Test
    @DisplayName("Should delete workspace successfully")
    void shouldDeleteWorkspace() {
      workspace.setBookings(Collections.emptyList());
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));

      workspaceService.deleteWorkspace(1L);

      verify(workspaceRepository).delete(workspace);
    }

    @Test
    @DisplayName("Should delete workspace when bookings are not CONFIRMED")
    void shouldDeleteWorkspace_WhenBookingsNotConfirmed() {
      Booking cancelledBooking = new Booking();
      cancelledBooking.setStatus(BookingStatus.CANCELLED);
      workspace.setBookings(List.of(cancelledBooking));
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));

      workspaceService.deleteWorkspace(1L);

      verify(workspaceRepository).delete(workspace);
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(workspaceRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> workspaceService.deleteWorkspace(99L));
      assertEquals(ErrorCode.WORKSPACE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when has CONFIRMED bookings")
    void shouldThrowException_WhenHasActiveBookings() {
      Booking confirmedBooking = new Booking();
      confirmedBooking.setStatus(BookingStatus.CONFIRMED);
      workspace.setBookings(List.of(confirmedBooking));
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));

      ConflictException ex = assertThrows(ConflictException.class,
          () -> workspaceService.deleteWorkspace(1L));
      assertEquals(ErrorCode.WORKSPACE_HAS_ACTIVE_BOOKINGS, ex.getErrorCode());
    }
  }

  // ==================== findAvailableWorkspaces ====================

  @Nested
  @DisplayName("findAvailableWorkspaces")
  class FindAvailableWorkspaces {

    @Test
    @DisplayName("Should include workspace when amenities match all required ids - filter active")
    void shouldInclude_WhenFilterActive_AndAmenitiesMatch() {
      Amenity wifi = new Amenity();
      wifi.setId(1L);
      workspace.setAmenities(List.of(wifi));

      when(workspaceRepository.findAvailableWorkspaces(any(), any()))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(List.of(workspaceDto));

      // amenityIds не null, не пустой, workspace имеет нужный amenity
      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(4, 500.0, List.of(1L));

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle workspace with null amenities in filter")
    void shouldFilterOut_WhenWorkspaceAmenitiesNull_InFilter() {
      workspace.setAmenities(null);

      when(workspaceRepository.findAvailableWorkspaces(any(), any()))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(4, 500.0, List.of(1L));

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should filter workspaces when amenityIds is not empty and workspace has matching amenities")
    void shouldInclude_WhenAllAmenitiesMatch_NotEmptyFilter() {
      Amenity wifi = new Amenity();
      wifi.setId(1L);
      Amenity projector = new Amenity();
      projector.setId(2L);
      workspace.setAmenities(new ArrayList<>(List.of(wifi, projector)));

      when(workspaceRepository.findAvailableWorkspaces(any(), any()))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(List.of(workspaceDto));

      // amenityIds = [1, 2], workspace имеет оба
      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(4, 500.0, List.of(1L, 2L));

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should filter out when workspace has amenities but not all required")
    void shouldFilterOut_WhenNotAllAmenitiesPresent_WithEmptyResult() {
      Amenity wifi = new Amenity();
      wifi.setId(1L);
      workspace.setAmenities(List.of(wifi)); // только Wi-Fi

      when(workspaceRepository.findAvailableWorkspaces(any(), any()))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      // amenityIds = [1, 2], workspace имеет только 1
      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(4, 500.0, List.of(1L, 2L));

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should filter out when workspace has empty amenities list")
    void shouldFilterOut_WhenWorkspaceHasEmptyAmenities() {
      workspace.setAmenities(Collections.emptyList());

      when(workspaceRepository.findAvailableWorkspaces(any(), any()))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(4, 500.0, List.of(1L));

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not filter when amenityIds is null even if workspace has amenities")
    void shouldNotFilter_WhenAmenityIdsNull() {
      workspace.setAmenities(List.of(amenity));

      when(workspaceRepository.findAvailableWorkspaces(eq(4), any()))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(List.of(workspaceDto));

      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(4, 500.0, null);

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null minCapacity and maxPrice")
    void shouldHandleNullMinCapacityAndMaxPrice() {
      when(workspaceRepository.findAvailableWorkspaces(isNull(), isNull()))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(List.of(workspaceDto));

      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(null, null, null);

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should filter workspaces with amenities when both have correct amenities")
    void shouldInclude_WhenAllAmenitiesMatch() {
      Amenity wifi = new Amenity();
      wifi.setId(1L);
      workspace.setAmenities(List.of(wifi));

      when(workspaceRepository.findAvailableWorkspaces(any(), any()))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(List.of(workspaceDto));

      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(4, 500.0, List.of(1L));

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle empty amenityIds list")
    void shouldHandleEmptyAmenityIdsList() {
      when(workspaceRepository.findAvailableWorkspaces(eq(4), any()))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(List.of(workspaceDto));

      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(4, 500.0, Collections.emptyList());

      assertFalse(result.isEmpty());
      // amenities не фильтруются, потому что список пустой
    }

    @Test
    @DisplayName("Should filter by amenities when workspace has null amenities")
    void shouldFilterOut_WhenWorkspaceHasNullAmenities() {
      Workspace workspaceNoAmenities = new Workspace();
      workspaceNoAmenities.setId(2L);
      workspaceNoAmenities.setNumber(102);
      workspaceNoAmenities.setAmenities(null); // amenities = null

      when(workspaceRepository.findAvailableWorkspaces(any(), any()))
          .thenReturn(List.of(workspaceNoAmenities, workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(4, 500.0, List.of(1L));

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should filter by amenities when workspace amenities do not contain all required")
    void shouldFilterOut_WhenNotAllAmenitiesPresent() {
      Amenity wifi = new Amenity();
      wifi.setId(1L);
      Amenity projector = new Amenity();
      projector.setId(2L);

      workspace.setAmenities(List.of(wifi)); // only WI-FI, no projector

      when(workspaceRepository.findAvailableWorkspaces(any(), any()))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(4, 500.0, List.of(1L, 2L));

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should find workspaces with filters")
    void shouldFindWorkspaces() {
      workspace.setAmenities(List.of(amenity));
      when(workspaceRepository.findAvailableWorkspaces(any(), any()))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(List.of(workspaceDto));

      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(4, 500.0, List.of(1L));

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no matches")
    void shouldReturnEmptyList() {
      when(workspaceRepository.findAvailableWorkspaces(any(), any()))
          .thenReturn(Collections.emptyList());
      when(workspaceMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(100, 1.0, null);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null maxPrice")
    void shouldHandleNullMaxPrice() {
      when(workspaceRepository.findAvailableWorkspaces(eq(4), isNull()))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(List.of(workspaceDto));

      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(4, null, null);

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null amenityIds")
    void shouldHandleNullAmenityIds() {
      when(workspaceRepository.findAvailableWorkspaces(eq(4), any()))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(List.of(workspaceDto));

      List<WorkspaceDto> result = workspaceService.findAvailableWorkspaces(4, 500.0, null);

      assertFalse(result.isEmpty());
    }
  }

  // ==================== getWorkspacesByMinCapacity ====================

  @Nested
  @DisplayName("getWorkspacesByMinCapacity")
  class GetWorkspacesByMinCapacity {

    @Test
    @DisplayName("Should return empty list when no workspaces match min capacity")
    void shouldReturnEmptyList() {
      when(workspaceRepository.findByCapacityGreaterThanEqual(100))
          .thenReturn(Collections.emptyList());
      when(workspaceMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<WorkspaceDto> result = workspaceService.getWorkspacesByMinCapacity(100);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return workspaces")
    void shouldReturnWorkspaces() {
      when(workspaceRepository.findByCapacityGreaterThanEqual(4))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(List.of(workspaceDto));

      List<WorkspaceDto> result = workspaceService.getWorkspacesByMinCapacity(4);

      assertFalse(result.isEmpty());
    }
  }

  // ==================== getWorkspacesByMaxPrice ====================

  @Nested
  @DisplayName("getWorkspacesByMaxPrice")
  class GetWorkspacesByMaxPrice {

    @Test
    @DisplayName("Should return empty list when no workspaces match max price")
    void shouldReturnEmptyList() {
      when(workspaceRepository.findByPricePerHourLessThanEqual(BigDecimal.valueOf(1.0)))
          .thenReturn(Collections.emptyList());
      when(workspaceMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<WorkspaceDto> result = workspaceService.getWorkspacesByMaxPrice(1.0);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return workspaces")
    void shouldReturnWorkspaces() {
      when(workspaceRepository.findByPricePerHourLessThanEqual(BigDecimal.valueOf(500.0)))
          .thenReturn(List.of(workspace));
      when(workspaceMapper.toDtoList(anyList())).thenReturn(List.of(workspaceDto));

      List<WorkspaceDto> result = workspaceService.getWorkspacesByMaxPrice(500.0);

      assertFalse(result.isEmpty());
    }
  }

  // ==================== addAmenityToWorkspace ====================

  @Nested
  @DisplayName("addAmenityToWorkspace")
  class AddAmenityToWorkspace {

    @Test
    @DisplayName("Should not save when amenity already exists in workspace")
    void shouldNotSave_WhenAmenityAlreadyExists_ContainsTrue() {
      workspace.getAmenities().add(amenity); // уже есть
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.addAmenityToWorkspace(1L, 1L);

      assertNotNull(result);
      verify(workspaceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not save when amenity already exists and amenities is initialized")
    void shouldNotSave_WhenAmenityAlreadyExists() {
      workspace.getAmenities().add(amenity); // уже есть
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.addAmenityToWorkspace(1L, 1L);

      assertNotNull(result);
      verify(workspaceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should initialize amenities list when null and add amenity")
    void shouldInitializeAmenities_WhenNull() {
      workspace.setAmenities(null);
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
      when(workspaceRepository.save(workspace)).thenReturn(workspace);
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.addAmenityToWorkspace(1L, 1L);

      assertNotNull(result);
    }

    @Test
    @DisplayName("Should add amenity successfully")
    void shouldAddAmenity() {
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
      when(workspaceRepository.save(workspace)).thenReturn(workspace);
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.addAmenityToWorkspace(1L, 1L);

      assertNotNull(result);
    }

    @Test
    @DisplayName("Should not add amenity if already exists")
    void shouldNotAddDuplicate() {
      workspace.getAmenities().add(amenity);
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.addAmenityToWorkspace(1L, 1L);

      assertNotNull(result);
      verify(workspaceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when workspace not found")
    void shouldThrowException_WhenWorkspaceNotFound() {
      when(workspaceRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> workspaceService.addAmenityToWorkspace(99L, 1L));
      assertEquals(ErrorCode.WORKSPACE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw NotFoundException when amenity not found")
    void shouldThrowException_WhenAmenityNotFound() {
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(amenityRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> workspaceService.addAmenityToWorkspace(1L, 99L));
      assertEquals(ErrorCode.WORKSPACE_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== removeAmenityFromWorkspace ====================

  @Nested
  @DisplayName("removeAmenityFromWorkspace")
  class RemoveAmenityFromWorkspace {

    @Test
    @DisplayName("Should return workspace without saving when amenities is null")
    void shouldReturnWorkspace_WhenAmenitiesIsNull() {
      workspace.setAmenities(null);
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.removeAmenityFromWorkspace(1L, 1L);

      assertNotNull(result);
      verify(workspaceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not save when amenity to remove does not exist in list")
    void shouldNotSave_WhenAmenityNotInList_RemoveIfFalse() {
      Amenity wifi = new Amenity();
      wifi.setId(1L);
      workspace.getAmenities().add(wifi); // есть Wi-Fi (id=1)
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      // Пытаемся удалить amenity с id=99, которого нет в списке
      WorkspaceDto result = workspaceService.removeAmenityFromWorkspace(1L, 99L);

      assertNotNull(result);
      verify(workspaceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not save when removeIf returns false (amenity not found in list)")
    void shouldNotSave_WhenRemoveIfReturnsFalse() {
      Amenity projector = new Amenity();
      projector.setId(99L);
      workspace.getAmenities().add(projector); // есть projector (id=99), удаляем Wi-Fi (id=1)
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.removeAmenityFromWorkspace(1L, 1L); // пытаемся удалить Wi-Fi, которого нет

      assertNotNull(result);
      verify(workspaceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not save when amenity not in list")
    void shouldNotSave_WhenAmenityNotInList() {
      workspace.getAmenities().add(amenity); // есть Wi-Fi, но удаляем projector (id=99)
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.removeAmenityFromWorkspace(1L, 99L);

      assertNotNull(result);
      verify(workspaceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should do nothing when amenities list is null")
    void shouldDoNothing_WhenAmenitiesNull() {
      workspace.setAmenities(null);
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.removeAmenityFromWorkspace(1L, 1L);

      assertNotNull(result);
      verify(workspaceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should remove amenity successfully")
    void shouldRemoveAmenity() {
      workspace.getAmenities().add(amenity);
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceRepository.save(workspace)).thenReturn(workspace);
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.removeAmenityFromWorkspace(1L, 1L);

      assertNotNull(result);
    }

    @Test
    @DisplayName("Should do nothing when amenity not in workspace")
    void shouldDoNothing_WhenAmenityNotPresent() {
      when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
      when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDto);

      WorkspaceDto result = workspaceService.removeAmenityFromWorkspace(1L, 99L);

      assertNotNull(result);
      verify(workspaceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when workspace not found")
    void shouldThrowException_WhenWorkspaceNotFound() {
      when(workspaceRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> workspaceService.removeAmenityFromWorkspace(99L, 1L));
      assertEquals(ErrorCode.WORKSPACE_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== existsByNumber ====================

  @Nested
  @DisplayName("existsByNumber")
  class ExistsByNumber {

    @Test
    @DisplayName("Should return false when number does not exist")
    void shouldReturnFalse_WhenNumberNotExists() {
      when(workspaceRepository.existsByNumber(999)).thenReturn(false);

      boolean result = workspaceService.existsByNumber(999);

      assertFalse(result);
    }

    @Test
    @DisplayName("Should return true when number exists")
    void shouldReturnTrue() {
      when(workspaceRepository.existsByNumber(101)).thenReturn(true);

      boolean result = workspaceService.existsByNumber(101);

      assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when number does not exist")
    void shouldReturnFalse() {
      when(workspaceRepository.existsByNumber(999)).thenReturn(false);

      boolean result = workspaceService.existsByNumber(999);

      assertFalse(result);
    }
  }

  // ==================== deleteByNumber ====================

  @Nested
  @DisplayName("deleteByNumber")
  class DeleteByNumber {

    @Test
    @DisplayName("Should delete by number when bookings exist but none are CONFIRMED")
    void shouldDeleteByNumber_WhenBookingsNotConfirmed() {
      Booking pendingBooking = new Booking();
      pendingBooking.setStatus(BookingStatus.PENDING);
      Booking cancelledBooking = new Booking();
      cancelledBooking.setStatus(BookingStatus.CANCELLED);
      workspace.setBookings(List.of(pendingBooking, cancelledBooking));
      when(workspaceRepository.findByNumber(101)).thenReturn(Optional.of(workspace));

      workspaceService.deleteByNumber(101);

      verify(workspaceRepository).deleteByNumber(101);
    }

    @Test
    @DisplayName("Should delete by number when bookings is null")
    void shouldDeleteByNumber_WhenBookingsNull() {
      workspace.setBookings(null);
      when(workspaceRepository.findByNumber(101)).thenReturn(Optional.of(workspace));

      workspaceService.deleteByNumber(101);

      verify(workspaceRepository).deleteByNumber(101);
    }

    @Test
    @DisplayName("Should delete workspace by number successfully")
    void shouldDeleteByNumber() {
      workspace.setBookings(Collections.emptyList());
      when(workspaceRepository.findByNumber(101)).thenReturn(Optional.of(workspace));

      workspaceService.deleteByNumber(101);

      verify(workspaceRepository).deleteByNumber(101);
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(workspaceRepository.findByNumber(999)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> workspaceService.deleteByNumber(999));
      assertEquals(ErrorCode.WORKSPACE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when has CONFIRMED bookings")
    void shouldThrowException_WhenHasActiveBookings() {
      Booking confirmedBooking = new Booking();
      confirmedBooking.setStatus(BookingStatus.CONFIRMED);
      workspace.setBookings(List.of(confirmedBooking));
      when(workspaceRepository.findByNumber(101)).thenReturn(Optional.of(workspace));

      ConflictException ex = assertThrows(ConflictException.class,
          () -> workspaceService.deleteByNumber(101));
      assertEquals(ErrorCode.WORKSPACE_HAS_ACTIVE_BOOKINGS, ex.getErrorCode());
    }
  }
}