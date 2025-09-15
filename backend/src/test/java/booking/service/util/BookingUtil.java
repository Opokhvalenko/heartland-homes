package booking.service.util;

import booking.service.dto.booking.BookingDto;
import booking.service.dto.booking.CreateBookingRequestDto;
import booking.service.model.BookingStatus;
import java.time.LocalDate;

public class BookingUtil {
    public static CreateBookingRequestDto createBookingRequestDto() {
        return new CreateBookingRequestDto()
                .setAccommodationId(4L)
                .setCheckInDate(LocalDate.now().plusDays(5))
                .setCheckOutDate(LocalDate.now().plusDays(7));
    }

    public static CreateBookingRequestDto createUpdatedBookingRequestDto() {
        return new CreateBookingRequestDto()
                .setAccommodationId(1L)
                .setCheckInDate(LocalDate.now().plusDays(10))
                .setCheckOutDate(LocalDate.now().plusDays(17));
    }

    public static CreateBookingRequestDto createInvalidBookingRequestDto() {
        return new CreateBookingRequestDto()
                .setAccommodationId(null)
                .setCheckInDate(LocalDate.parse("2025-10-06"))
                .setCheckOutDate(LocalDate.parse("2025-10-02"));
    }

    public static BookingDto createBookingDto(Long id) {
        return new BookingDto()
                .setId(id)
                .setAccommodationId(1L)
                .setStatus(BookingStatus.CONFIRMED)
                .setCheckInDate(LocalDate.parse("2025-09-01"))
                .setCheckOutDate(LocalDate.parse("2025-09-05"));
    }
}


