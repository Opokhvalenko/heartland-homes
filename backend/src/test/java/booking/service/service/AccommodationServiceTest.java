package booking.service.service;

import static booking.service.util.AccommodationUtil.createAccommodation2;
import static booking.service.util.AccommodationUtil.createAccommodationDto2;
import static booking.service.util.AccommodationUtil.createAccommodationRequestDto2;
import static booking.service.util.UserUtil.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import booking.service.dto.accommodation.AccommodationDto;
import booking.service.dto.accommodation.AccommodationSearchParametersDto;
import booking.service.dto.accommodation.CreateAccommodationRequestDto;
import booking.service.exception.EntityNotFoundException;
import booking.service.mapper.AccommodationMapper;
import booking.service.model.Accommodation;
import booking.service.model.AccommodationStatus;
import booking.service.model.AccommodationType;
import booking.service.model.User;
import booking.service.repository.accommodation.AccommodationRepository;
import booking.service.repository.accommodation.AccommodationSpecificationBuilder;
import booking.service.service.impl.AccommodationServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {

    @InjectMocks
    private AccommodationServiceImpl accommodationService;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private AccommodationMapper accommodationMapper;
    @Mock
    private AccommodationSpecificationBuilder specificationBuilder;

    @Test
    @DisplayName("save should save and return AccommodationDto")
    void save_returnsSavedAccommodationDto() {
        CreateAccommodationRequestDto requestDto = createAccommodationRequestDto2();
        Accommodation accommodation = createAccommodation2(1L);
        AccommodationDto dto = createAccommodationDto2(1L);
        User mockUser = createUser(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);

        when(accommodationMapper.toEntity(requestDto)).thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(accommodationMapper.toDto(accommodation)).thenReturn(dto);

        AccommodationDto result = accommodationService.save(requestDto, authentication);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(accommodationMapper).toEntity(requestDto);
        verify(accommodationRepository).save(accommodation);
        verify(accommodationMapper).toDto(accommodation);
    }

    @Test
    @DisplayName("findAll should return page of AccommodationDto")
    void findAll_returnsPageOfAccommodationDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Accommodation accommodation = createAccommodation2(1L);
        AccommodationDto dto = createAccommodationDto2(1L);
        Page<Accommodation> page = new PageImpl<>(List.of(accommodation));

        when(accommodationRepository.findAll(pageable)).thenReturn(page);
        when(accommodationMapper.toDto(accommodation)).thenReturn(dto);

        Page<AccommodationDto> result = accommodationService.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(dto, result.getContent().get(0));
        verify(accommodationRepository).findAll(pageable);
        verify(accommodationMapper).toDto(accommodation);
    }

    @Test
    @DisplayName("findById should return AccommodationDto when accommodation exists")
    void findById_existingAccommodation_returnsDto() {
        Long id = 1L;
        Accommodation accommodation = createAccommodation2(id);
        AccommodationDto dto = createAccommodationDto2(id);

        when(accommodationRepository.findById(id)).thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toDto(accommodation)).thenReturn(dto);

        AccommodationDto result = accommodationService.findById(id);

        assertEquals(dto, result);
        verify(accommodationRepository).findById(id);
        verify(accommodationMapper).toDto(accommodation);
    }

    @Test
    @DisplayName("findById should throw if accommodation not found")
    void findById_notFound_throwsException() {
        Long id = 1L;
        when(accommodationRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.findById(id));

        assertEquals("Can't find accommodation by id: " + id, ex.getMessage());
        verify(accommodationRepository).findById(id);
    }

    @Test
    @DisplayName("update should update and return AccommodationDto")
    void update_existingAccommodation_returnsUpdatedDto() {
        Long id = 1L;
        CreateAccommodationRequestDto requestDto = createAccommodationRequestDto2();
        Accommodation accommodation = createAccommodation2(id);
        AccommodationDto updatedDto = createAccommodationDto2(id);

        when(accommodationRepository.findById(id)).thenReturn(Optional.of(accommodation));
        doNothing().when(accommodationMapper).updateAccommodationFromDto(requestDto, accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(accommodationMapper.toDto(accommodation)).thenReturn(updatedDto);

        AccommodationDto result = accommodationService.update(id, requestDto);

        assertEquals(updatedDto, result);
        verify(accommodationRepository).findById(id);
        verify(accommodationMapper).updateAccommodationFromDto(requestDto, accommodation);
        verify(accommodationRepository).save(accommodation);
        verify(accommodationMapper).toDto(accommodation);
    }

    @Test
    @DisplayName("update should throw if accommodation not found")
    void update_notFound_throwsException() {
        Long id = 1L;
        CreateAccommodationRequestDto requestDto = createAccommodationRequestDto2();
        when(accommodationRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.update(id, requestDto));

        assertEquals("Can't find accommodation by id: " + id, ex.getMessage());
        verify(accommodationRepository).findById(id);
    }

    @Test
    @DisplayName("deleteById should delete when accommodation exists")
    void deleteById_existing_callsRepository() {
        Long id = 1L;
        when(accommodationRepository.existsById(id)).thenReturn(true);
        doNothing().when(accommodationRepository).deleteById(id);

        accommodationService.deleteById(id);

        verify(accommodationRepository).existsById(id);
        verify(accommodationRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteById should throw if accommodation not found")
    void deleteById_notFound_throwsException() {
        Long id = 1L;
        when(accommodationRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.deleteById(id));

        assertEquals("Can't find accommodation by id: " + id, ex.getMessage());
        verify(accommodationRepository).existsById(id);
    }

    @Test
    @DisplayName("search should return page of AccommodationDto")
    void search_returnsPageOfAccommodationDto() {
        AccommodationSearchParametersDto params = new AccommodationSearchParametersDto(
                new String[]{"Kyiv"}, new String[]{"Large"},
                new AccommodationType[]{AccommodationType.APARTMENT},
                new AccommodationStatus[]{AccommodationStatus.PERMITTED},
                BigDecimal.valueOf(100), BigDecimal.valueOf(300)
        );

        Pageable pageable = PageRequest.of(0, 10);
        Accommodation accommodation = createAccommodation2(1L);
        AccommodationDto dto = createAccommodationDto2(1L);
        Specification<Accommodation> spec = mock(Specification.class);

        when(specificationBuilder.build(params)).thenReturn(spec);
        when(accommodationRepository.findAll(spec, pageable))
                .thenReturn(new PageImpl<>(List.of(accommodation)));
        when(accommodationMapper.toDto(accommodation)).thenReturn(dto);

        Page<AccommodationDto> result = accommodationService.search(params, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(dto, result.getContent().get(0));
        verify(specificationBuilder).build(params);
        verify(accommodationRepository).findAll(spec, pageable);
        verify(accommodationMapper).toDto(accommodation);
    }
}
