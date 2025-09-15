package booking.service.service;

import booking.service.dto.accommodation.AccommodationDto;
import booking.service.dto.accommodation.AccommodationSearchParametersDto;
import booking.service.dto.accommodation.CreateAccommodationRequestDto;
import booking.service.dto.accommodation.UpdateAccommodationStatusDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface AccommodationService {

    AccommodationDto save(CreateAccommodationRequestDto requestDto, Authentication authentication);

    Page<AccommodationDto> findAll(Pageable pageable);

    AccommodationDto findById(Long id);

    AccommodationDto update(Long id, CreateAccommodationRequestDto requestDto);

    void deleteById(Long id);

    Page<AccommodationDto> search(AccommodationSearchParametersDto params, Pageable pageable);

    AccommodationDto updateStatus(Long id, UpdateAccommodationStatusDto requestDto);

    Page<AccommodationDto> getMyAccommodations(Authentication authentication, Pageable pageable);
}
