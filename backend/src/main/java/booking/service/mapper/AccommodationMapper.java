package booking.service.mapper;

import booking.service.config.MapperConfig;
import booking.service.dto.accommodation.AccommodationDto;
import booking.service.dto.accommodation.CreateAccommodationRequestDto;
import booking.service.model.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {

    @Mapping(source = "owner.email", target = "ownerEmail")
    AccommodationDto toDto(Accommodation accommodation);

    Accommodation toEntity(CreateAccommodationRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "owner", ignore = true)
    void updateAccommodationFromDto(CreateAccommodationRequestDto requestDto,
            @MappingTarget Accommodation accommodation);
}
