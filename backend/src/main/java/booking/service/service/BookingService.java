package booking.service.service;

import booking.service.dto.booking.BookingDto;
import booking.service.dto.booking.CreateBookingRequestDto;
import booking.service.dto.booking.UpdateBookingStatusDto;
import booking.service.model.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface BookingService {

    BookingDto create(CreateBookingRequestDto requestDto, Authentication authentication);

    Page<BookingDto> findByUserAndStatus(Long userId, BookingStatus status,
            Authentication authentication, Pageable pageable);

    Page<BookingDto> findMyBookings(Pageable pageable, Authentication authentication);

    BookingDto findById(Long id, Authentication authentication);

    BookingDto update(Long id, CreateBookingRequestDto requestDto, Authentication authentication);

    void cancel(Long id, Authentication authentication);

    BookingDto updateStatus(Long id, UpdateBookingStatusDto requestDto);
}
