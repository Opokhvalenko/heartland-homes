package booking.service.mapper;

import booking.service.config.MapperConfig;
import booking.service.dto.booking.BookingDto;
import booking.service.dto.booking.CreateBookingRequestDto;
import booking.service.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {

    @Mapping(source = "accommodation.id", target = "accommodationId")
    @Mapping(source = "user.id", target = "userId")
    BookingDto toDto(Booking booking);

    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "user", ignore = true)
    Booking toEntity(CreateBookingRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateBookingFromDto(CreateBookingRequestDto requestDto,
            @MappingTarget Booking booking);
}
