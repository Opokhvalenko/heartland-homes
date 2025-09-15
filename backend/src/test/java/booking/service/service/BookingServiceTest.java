package booking.service.service;

import static booking.service.util.BookingUtil.createBookingDto;
import static booking.service.util.BookingUtil.createBookingRequestDto;
import static booking.service.util.BookingUtil.createUpdatedBookingRequestDto;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import booking.service.dto.booking.BookingDto;
import booking.service.dto.booking.CreateBookingRequestDto;
import booking.service.exception.UnpaidPaymentException;
import booking.service.mapper.BookingMapper;
import booking.service.model.Accommodation;
import booking.service.model.Booking;
import booking.service.model.BookingStatus;
import booking.service.model.User;
import booking.service.repository.accommodation.AccommodationRepository;
import booking.service.repository.booking.BookingRepository;
import booking.service.repository.payment.PaymentRepository;
import booking.service.repository.user.UserRepository;
import booking.service.service.impl.BookingServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Accommodation accommodation;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = new User().setId(2L).setEmail("test@example.com");
        accommodation = new Accommodation().setId(4L).setCity("Kyiv").setLocation("Center");
        booking = new Booking().setId(1L).setUser(user).setAccommodation(accommodation);
        bookingDto = createBookingDto(1L);
    }

    @Test
    void createBooking_success() {
        CreateBookingRequestDto requestDto = createBookingRequestDto();
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(user.getEmail());

        when(accommodationRepository.findById(4L)).thenReturn(Optional.of(accommodation));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(paymentRepository.existsUnpaidPaymentsByUserId(user.getId())).thenReturn(false);
        when(bookingMapper.toEntity(requestDto)).thenReturn(new Booking());
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.create(requestDto, auth);

        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
        verify(notificationService).sendMessage(contains("Створено нове бронювання"));
    }

    @Test
    void createBooking_withUnpaidPayments_throwsException() {

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(user.getEmail());

        when(accommodationRepository.findById(4L)).thenReturn(Optional.of(accommodation));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(paymentRepository.existsUnpaidPaymentsByUserId(user.getId())).thenReturn(true);

        CreateBookingRequestDto requestDto = createBookingRequestDto();
        assertThrows(UnpaidPaymentException.class,
                () -> bookingService.create(requestDto, auth));
    }

    @Test
    void findById_asOwner_success() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.findById(1L, auth);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_otherUser_throwsAccessDenied() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(new User().setId(99L));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class,
                () -> bookingService.findById(1L, auth));
    }

    @Test
    void updateBooking_asOwner_success() {
        CreateBookingRequestDto requestDto = createUpdatedBookingRequestDto()
                .setAccommodationId(accommodation.getId());

        booking.setUser(user).setAccommodation(accommodation);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.existsByAccommodationIdAndDateOverlapExcludingBooking(
                anyLong(), any(), any(), anyLong())).thenReturn(false);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.update(1L, requestDto, auth);

        assertNotNull(result);
        verify(bookingRepository).save(booking);
    }

    @Test
    void updateBooking_asOwnerChangingAccommodation_throwsAccessDenied() {

        booking.setUser(user).setAccommodation(accommodation);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        CreateBookingRequestDto requestDto = createUpdatedBookingRequestDto();
        assertThrows(AccessDeniedException.class,
                () -> bookingService.update(1L, requestDto, auth));
    }

    @Test
    void cancelBooking_asOwner_success() {
        booking.setStatus(BookingStatus.CONFIRMED).setUser(user);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.cancel(1L, auth);

        assertEquals(BookingStatus.CANCELED, booking.getStatus());
        verify(notificationService).sendMessage(contains("Бронювання скасовано"));
    }

    @Test
    void cancelBooking_alreadyCanceled_throwsException() {
        booking.setStatus(BookingStatus.CANCELED).setUser(user);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class,
                () -> bookingService.cancel(1L, auth));
    }
}
