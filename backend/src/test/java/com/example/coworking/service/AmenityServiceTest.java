package com.example.coworking.service;

import com.example.coworking.dto.AmenityCreateDto;
import com.example.coworking.dto.AmenityDto;
import com.example.coworking.dto.AmenityUpdateDto;
import com.example.coworking.exception.ConflictException;
import com.example.coworking.exception.ErrorCode;
import com.example.coworking.exception.NotFoundException;
import com.example.coworking.mapper.AmenityMapper;
import com.example.coworking.model.Amenity;
import com.example.coworking.model.Workspace;
import com.example.coworking.repository.AmenityRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AmenityService tests")
class AmenityServiceTest {

  @Mock
  private AmenityRepository amenityRepository;
  @Mock
  private AmenityMapper amenityMapper;

  @InjectMocks
  private AmenityService amenityService;

  private Amenity amenity;
  private AmenityDto amenityDto;
  private AmenityCreateDto createDto;
  private AmenityUpdateDto updateDto;

  @BeforeEach
  void setUp() {
    reset(amenityRepository, amenityMapper);

    amenity = new Amenity();
    amenity.setId(1L);
    amenity.setName("Wi-Fi");
    amenity.setDescription("High-speed internet");

    amenityDto = new AmenityDto();
    amenityDto.setId(1L);
    amenityDto.setName("Wi-Fi");
    amenityDto.setDescription("High-speed internet");

    createDto = new AmenityCreateDto();
    createDto.setName("Wi-Fi");
    createDto.setDescription("High-speed internet");

    updateDto = new AmenityUpdateDto();
    updateDto.setName("Wi-Fi Premium");
    updateDto.setDescription("Ultra high-speed internet");
  }

  // ==================== getAllAmenities ====================

  @Nested
  @DisplayName("getAllAmenities")
  class GetAllAmenities {

    @Test
    @DisplayName("Should return page of amenities")
    void shouldReturnPage() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<Amenity> page = new PageImpl<>(List.of(amenity), pageable, 1);
      when(amenityRepository.findAll(pageable)).thenReturn(page);
      when(amenityMapper.toDto(any())).thenReturn(amenityDto);

      Page<AmenityDto> result = amenityService.getAllAmenities(pageable);

      assertNotNull(result);
      assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when no amenities")
    void shouldReturnEmptyPage() {
      Pageable pageable = PageRequest.of(0, 20);
      Page<Amenity> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
      when(amenityRepository.findAll(pageable)).thenReturn(emptyPage);

      Page<AmenityDto> result = amenityService.getAllAmenities(pageable);

      assertNotNull(result);
      assertEquals(0, result.getTotalElements());
    }
  }

  // ==================== getAmenityById ====================

  @Nested
  @DisplayName("getAmenityById")
  class GetAmenityById {

    @Test
    @DisplayName("Should return amenity when found")
    void shouldReturnAmenity_WhenFound() {
      when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
      when(amenityMapper.toDto(amenity)).thenReturn(amenityDto);

      AmenityDto result = amenityService.getAmenityById(1L);

      assertNotNull(result);
      assertEquals("Wi-Fi", result.getName());
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(amenityRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> amenityService.getAmenityById(99L));
      assertEquals(ErrorCode.AMENITY_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== getAmenityByName ====================

  @Nested
  @DisplayName("getAmenityByName")
  class GetAmenityByName {

    @Test
    @DisplayName("Should return amenity when found")
    void shouldReturnAmenity_WhenFound() {
      when(amenityRepository.findByName("Wi-Fi")).thenReturn(Optional.of(amenity));
      when(amenityMapper.toDto(amenity)).thenReturn(amenityDto);

      AmenityDto result = amenityService.getAmenityByName("Wi-Fi");

      assertNotNull(result);
      assertEquals("Wi-Fi", result.getName());
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(amenityRepository.findByName("Unknown")).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> amenityService.getAmenityByName("Unknown"));
      assertEquals(ErrorCode.AMENITY_NOT_FOUND, ex.getErrorCode());
    }
  }

  // ==================== createAmenity ====================

  @Nested
  @DisplayName("createAmenity")
  class CreateAmenity {

    @Test
    @DisplayName("Should create amenity successfully")
    void shouldCreateAmenity() {
      when(amenityRepository.existsByName("Wi-Fi")).thenReturn(false);
      when(amenityMapper.toEntity(createDto)).thenReturn(amenity);
      when(amenityRepository.save(amenity)).thenReturn(amenity);
      when(amenityMapper.toDto(amenity)).thenReturn(amenityDto);

      AmenityDto result = amenityService.createAmenity(createDto);

      assertNotNull(result);
      assertEquals("Wi-Fi", result.getName());
    }

    @Test
    @DisplayName("Should throw ConflictException when name exists")
    void shouldThrowException_WhenNameExists() {
      when(amenityRepository.existsByName("Wi-Fi")).thenReturn(true);

      ConflictException ex = assertThrows(ConflictException.class,
          () -> amenityService.createAmenity(createDto));
      assertEquals(ErrorCode.AMENITY_EXISTS, ex.getErrorCode());
    }
  }

  // ==================== updateAmenity ====================

  @Nested
  @DisplayName("updateAmenity")
  class UpdateAmenity {

    @Test
    @DisplayName("Should update amenity successfully")
    void shouldUpdateAmenity() {
      when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
      when(amenityRepository.existsByName("Wi-Fi Premium")).thenReturn(false);
      when(amenityRepository.save(amenity)).thenReturn(amenity);
      when(amenityMapper.toDto(amenity)).thenReturn(amenityDto);

      AmenityDto result = amenityService.updateAmenity(1L, updateDto);

      assertNotNull(result);
      verify(amenityMapper).updateEntity(updateDto, amenity);
    }

    @Test
    @DisplayName("Should throw NotFoundException when amenity not found")
    void shouldThrowException_WhenNotFound() {
      when(amenityRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> amenityService.updateAmenity(99L, updateDto));
      assertEquals(ErrorCode.AMENITY_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when new name already exists")
    void shouldThrowException_WhenNewNameExists() {
      updateDto.setName("Other");
      when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
      when(amenityRepository.existsByName("Other")).thenReturn(true);

      ConflictException ex = assertThrows(ConflictException.class,
          () -> amenityService.updateAmenity(1L, updateDto));
      assertEquals(ErrorCode.AMENITY_EXISTS, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should allow update when name unchanged")
    void shouldAllowUpdate_WhenNameUnchanged() {
      updateDto.setName("Wi-Fi"); // same as current name
      when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
      when(amenityRepository.save(amenity)).thenReturn(amenity);
      when(amenityMapper.toDto(amenity)).thenReturn(amenityDto);

      AmenityDto result = amenityService.updateAmenity(1L, updateDto);

      assertNotNull(result);
      // existsByName should NOT be called because name hasn't changed
      verify(amenityRepository, never()).existsByName(any());
    }

    @Test
    @DisplayName("Should allow update when name is null in updateDto")
    void shouldAllowUpdate_WhenNameIsNull() {
      updateDto.setName(null);
      when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
      when(amenityRepository.save(amenity)).thenReturn(amenity);
      when(amenityMapper.toDto(amenity)).thenReturn(amenityDto);

      AmenityDto result = amenityService.updateAmenity(1L, updateDto);

      assertNotNull(result);
      verify(amenityRepository, never()).existsByName(any());
    }
  }

  // ==================== deleteAmenity ====================

  @Nested
  @DisplayName("deleteAmenity")
  class DeleteAmenity {

    @Test
    @DisplayName("Should delete amenity successfully")
    void shouldDeleteAmenity() {
      amenity.setWorkspaces(Collections.emptyList());
      when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));

      amenityService.deleteAmenity(1L);

      verify(amenityRepository).delete(amenity);
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(amenityRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> amenityService.deleteAmenity(99L));
      assertEquals(ErrorCode.AMENITY_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when used in workspaces")
    void shouldThrowException_WhenUsedInWorkspaces() {
      Workspace ws = new Workspace();
      amenity.setWorkspaces(List.of(ws));
      when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));

      ConflictException ex = assertThrows(ConflictException.class,
          () -> amenityService.deleteAmenity(1L));
      assertEquals(ErrorCode.AMENITY_IS_USED_IN_WORKSPACE, ex.getErrorCode());
    }
  }

  // ==================== deleteAmenityByName ====================

  @Nested
  @DisplayName("deleteAmenityByName")
  class DeleteAmenityByName {

    @Test
    @DisplayName("Should delete amenity by name successfully")
    void shouldDeleteAmenityByName() {
      amenity.setWorkspaces(Collections.emptyList());
      when(amenityRepository.findByName("Wi-Fi")).thenReturn(Optional.of(amenity));

      amenityService.deleteAmenityByName("Wi-Fi");

      verify(amenityRepository).deleteByName("Wi-Fi");
    }

    @Test
    @DisplayName("Should throw NotFoundException when not found")
    void shouldThrowException_WhenNotFound() {
      when(amenityRepository.findByName("Unknown")).thenReturn(Optional.empty());

      NotFoundException ex = assertThrows(NotFoundException.class,
          () -> amenityService.deleteAmenityByName("Unknown"));
      assertEquals(ErrorCode.AMENITY_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should throw ConflictException when used in workspaces")
    void shouldThrowException_WhenUsedInWorkspaces() {
      Workspace ws = new Workspace();
      amenity.setWorkspaces(List.of(ws));
      when(amenityRepository.findByName("Wi-Fi")).thenReturn(Optional.of(amenity));

      ConflictException ex = assertThrows(ConflictException.class,
          () -> amenityService.deleteAmenityByName("Wi-Fi"));
      assertEquals(ErrorCode.AMENITY_IS_USED_IN_WORKSPACE, ex.getErrorCode());
    }
  }

  // ==================== searchAmenitiesByName ====================

  @Nested
  @DisplayName("searchAmenitiesByName")
  class SearchAmenitiesByName {

    @Test
    @DisplayName("Should return matching amenities")
    void shouldReturnMatchingAmenities() {
      when(amenityRepository.findByNameContainingIgnoreCase("Wi"))
          .thenReturn(List.of(amenity));
      when(amenityMapper.toDtoList(any())).thenReturn(List.of(amenityDto));

      List<AmenityDto> result = amenityService.searchAmenitiesByName("Wi");

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no matches")
    void shouldReturnEmptyList() {
      when(amenityRepository.findByNameContainingIgnoreCase("XYZ"))
          .thenReturn(Collections.emptyList());
      when(amenityMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<AmenityDto> result = amenityService.searchAmenitiesByName("XYZ");

      assertTrue(result.isEmpty());
    }
  }

  // ==================== searchAmenitiesByDescription ====================

  @Nested
  @DisplayName("searchAmenitiesByDescription")
  class SearchAmenitiesByDescription {

    @Test
    @DisplayName("Should return matching amenities")
    void shouldReturnMatchingAmenities() {
      when(amenityRepository.findByDescriptionContainingIgnoreCase("speed"))
          .thenReturn(List.of(amenity));
      when(amenityMapper.toDtoList(any())).thenReturn(List.of(amenityDto));

      List<AmenityDto> result = amenityService.searchAmenitiesByDescription("speed");

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no matches")
    void shouldReturnEmptyList() {
      when(amenityRepository.findByDescriptionContainingIgnoreCase("nonexistent"))
          .thenReturn(Collections.emptyList());
      when(amenityMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<AmenityDto> result = amenityService.searchAmenitiesByDescription("nonexistent");

      assertTrue(result.isEmpty());
    }
  }

  // ==================== getAmenitiesByMinCapacity ====================

  @Nested
  @DisplayName("getAmenitiesByMinCapacity")
  class GetAmenitiesByMinCapacity {

    @Test
    @DisplayName("Should return matching amenities")
    void shouldReturnMatchingAmenities() {
      when(amenityRepository.findByWorkspaceMinCapacity(4))
          .thenReturn(List.of(amenity));
      when(amenityMapper.toDtoList(any())).thenReturn(List.of(amenityDto));

      List<AmenityDto> result = amenityService.getAmenitiesByMinCapacity(4);

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no matches")
    void shouldReturnEmptyList() {
      when(amenityRepository.findByWorkspaceMinCapacity(100))
          .thenReturn(Collections.emptyList());
      when(amenityMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<AmenityDto> result = amenityService.getAmenitiesByMinCapacity(100);

      assertTrue(result.isEmpty());
    }
  }

  // ==================== getAmenitiesByMaxPrice ====================

  @Nested
  @DisplayName("getAmenitiesByMaxPrice")
  class GetAmenitiesByMaxPrice {

    @Test
    @DisplayName("Should return matching amenities")
    void shouldReturnMatchingAmenities() {
      when(amenityRepository.findByWorkspaceMaxPrice(BigDecimal.valueOf(500.0)))
          .thenReturn(List.of(amenity));
      when(amenityMapper.toDtoList(any())).thenReturn(List.of(amenityDto));

      List<AmenityDto> result = amenityService.getAmenitiesByMaxPrice(500.0);

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no matches")
    void shouldReturnEmptyList() {
      when(amenityRepository.findByWorkspaceMaxPrice(BigDecimal.valueOf(1.0)))
          .thenReturn(Collections.emptyList());
      when(amenityMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<AmenityDto> result = amenityService.getAmenitiesByMaxPrice(1.0);

      assertTrue(result.isEmpty());
    }
  }

  // ==================== getAllAmenityNames ====================

  @Nested
  @DisplayName("getAllAmenityNames")
  class GetAllAmenityNames {

    @Test
    @DisplayName("Should return all amenity names")
    void shouldReturnAllNames() {
      when(amenityRepository.findAllNames()).thenReturn(List.of("Wi-Fi", "Projector"));

      List<String> result = amenityService.getAllAmenityNames();

      assertEquals(2, result.size());
      assertTrue(result.contains("Wi-Fi"));
    }

    @Test
    @DisplayName("Should return empty list when no amenities")
    void shouldReturnEmptyList() {
      when(amenityRepository.findAllNames()).thenReturn(Collections.emptyList());

      List<String> result = amenityService.getAllAmenityNames();

      assertTrue(result.isEmpty());
    }
  }

  // ==================== getAmenitiesNotInWorkspace ====================

  @Nested
  @DisplayName("getAmenitiesNotInWorkspace")
  class GetAmenitiesNotInWorkspace {

    @Test
    @DisplayName("Should return amenities not in workspace")
    void shouldReturnAmenities() {
      when(amenityRepository.findNotInWorkspace(1L)).thenReturn(List.of(amenity));
      when(amenityMapper.toDtoList(any())).thenReturn(List.of(amenityDto));

      List<AmenityDto> result = amenityService.getAmenitiesNotInWorkspace(1L);

      assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list")
    void shouldReturnEmptyList() {
      when(amenityRepository.findNotInWorkspace(1L)).thenReturn(Collections.emptyList());
      when(amenityMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<AmenityDto> result = amenityService.getAmenitiesNotInWorkspace(1L);

      assertTrue(result.isEmpty());
    }
  }

  // ==================== getAmenityStatistics ====================

  @Nested
  @DisplayName("getAmenityStatistics")
  class GetAmenityStatistics {

    @Test
    @DisplayName("Should return statistics")
    void shouldReturnStatistics() {
      Object[] stat = new Object[]{"Wi-Fi", 5L};

      List<Object[]> mockResult = new java.util.ArrayList<>();
      mockResult.add(stat);

      when(amenityRepository.countWorkspaces()).thenReturn(mockResult);

      List<Object[]> result = amenityService.getAmenityStatistics();

      assertEquals(1, result.size());
      assertEquals("Wi-Fi", result.get(0)[0]);
      assertEquals(5L, result.get(0)[1]);
    }

    @Test
    @DisplayName("Should return empty list when no amenities")
    void shouldReturnEmptyList() {
      when(amenityRepository.countWorkspaces()).thenReturn(Collections.emptyList());

      List<Object[]> result = amenityService.getAmenityStatistics();

      assertTrue(result.isEmpty());
    }
  }
}