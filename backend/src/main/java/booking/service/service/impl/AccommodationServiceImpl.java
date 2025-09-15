package booking.service.service.impl;

import booking.service.dto.accommodation.AccommodationDto;
import booking.service.dto.accommodation.AccommodationSearchParametersDto;
import booking.service.dto.accommodation.CreateAccommodationRequestDto;
import booking.service.dto.accommodation.UpdateAccommodationStatusDto;
import booking.service.exception.EntityNotFoundException;
import booking.service.mapper.AccommodationMapper;
import booking.service.model.Accommodation;
import booking.service.model.AccommodationStatus;
import booking.service.model.RoleName;
import booking.service.model.User;
import booking.service.repository.accommodation.AccommodationRepository;
import booking.service.repository.accommodation.AccommodationSpecificationBuilder;
import booking.service.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final AccommodationSpecificationBuilder accommodationSpecificationBuilder;

    @Override
    public AccommodationDto save(CreateAccommodationRequestDto requestDto,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        Accommodation accommodation = accommodationMapper.toEntity(requestDto);
        accommodation.setOwner(currentUser);

        if (isManager(currentUser)) {
            accommodation.setAccommodationStatus(AccommodationStatus.PERMITTED);
        } else {
            accommodation.setAccommodationStatus(AccommodationStatus.REQUIRES_VERIFICATION);
        }

        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public Page<AccommodationDto> findAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable)
                .map(accommodationMapper::toDto);
    }

    @Override
    public AccommodationDto findById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find accommodation by id: " + id)
        );
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public AccommodationDto update(Long id, CreateAccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find accommodation by id: " + id)
        );
        accommodationMapper.updateAccommodationFromDto(requestDto, accommodation);
        accommodationRepository.save(accommodation);
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public void deleteById(Long id) {
        if (!accommodationRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find accommodation by id: " + id);
        }
        accommodationRepository.deleteById(id);
    }

    @Override
    public Page<AccommodationDto> search(AccommodationSearchParametersDto params,
            Pageable pageable) {
        Specification<Accommodation> accommodationSpecificationSpecification =
                accommodationSpecificationBuilder.build(params);
        Page<Accommodation> accommodationPagePage = accommodationRepository
                .findAll(accommodationSpecificationSpecification, pageable);
        return accommodationPagePage.map(accommodationMapper::toDto);
    }

    @Override
    public AccommodationDto updateStatus(Long id, UpdateAccommodationStatusDto requestDto) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find accommodation by id: " + id)
        );
        accommodation.setAccommodationStatus(requestDto.getStatus());
        accommodationRepository.save(accommodation);
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public Page<AccommodationDto> getMyAccommodations(Authentication authentication,
            Pageable pageable) {
        User currentUser = (User) authentication.getPrincipal();

        return accommodationRepository.findByOwner(currentUser, pageable)
                .map(accommodationMapper::toDto);
    }

    private boolean isManager(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.MANAGER);
    }
}
