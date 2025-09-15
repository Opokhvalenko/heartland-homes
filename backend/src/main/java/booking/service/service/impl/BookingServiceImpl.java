package booking.service.service.impl;

import booking.service.dto.booking.BookingDto;
import booking.service.dto.booking.CreateBookingRequestDto;
import booking.service.dto.booking.UpdateBookingStatusDto;
import booking.service.exception.EntityNotFoundException;
import booking.service.exception.UnpaidPaymentException;
import booking.service.mapper.BookingMapper;
import booking.service.model.Accommodation;
import booking.service.model.Booking;
import booking.service.model.BookingStatus;
import booking.service.model.RoleName;
import booking.service.model.User;
import booking.service.repository.accommodation.AccommodationRepository;
import booking.service.repository.booking.BookingRepository;
import booking.service.repository.payment.PaymentRepository;
import booking.service.repository.user.UserRepository;
import booking.service.service.BookingService;
import booking.service.service.NotificationService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public BookingDto create(CreateBookingRequestDto requestDto, Authentication authentication) {
        validateBookingDates(requestDto.getCheckInDate(), requestDto.getCheckOutDate());
        Accommodation accommodation = accommodationRepository
                .findById(requestDto.getAccommodationId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Accommodation not found with id: " + requestDto.getAccommodationId()));

        if (bookingRepository.existsByAccommodationIdAndDateOverlap(
                accommodation.getId(),
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate()
        )) {
            throw new IllegalStateException("Accommodation is already booked for these dates.");
        }

        User bookingUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with email: " + authentication.getName()));

        if (paymentRepository.existsUnpaidPaymentsByUserId(bookingUser.getId())) {
            throw new UnpaidPaymentException(
                    "Cannot create new booking. You have unpaid payments. "
                            + "Please complete your pending payments first.");
        }

        Booking booking = bookingMapper.toEntity(requestDto)
                .setAccommodation(accommodation)
                .setUser(bookingUser)
                .setStatus(BookingStatus.PENDING);

        bookingRepository.save(booking);

        notificationService.sendMessage(String.format(
                "🏨 Створено нове бронювання!\n"
                        + "Користувач: %s\n"
                        + "Житло: %s (%s)\n"
                        + "Дата заїзду: %s\n"
                        + "Дата виїзду: %s",
                bookingUser.getEmail(),
                accommodation.getLocation(),
                accommodation.getCity(),
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        ));

        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingDto> findByUserAndStatus(Long userId, BookingStatus status,
            Authentication authentication, Pageable pageable) {
        User currentUser = (User) authentication.getPrincipal();
        boolean isManager = isManager(currentUser);

        if (!isManager && userId != null && !userId.equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to view bookings of another user.");
        }

        if (!isManager && userId == null) {
            userId = currentUser.getId();
        }

        Page<Booking> bookings;

        if (userId != null && status != null) {
            bookings = bookingRepository.findByUserIdAndStatus(userId, status, pageable);
        } else if (userId != null) {
            bookings = bookingRepository.findByUserId(userId, pageable);
        } else if (status != null) {
            bookings = bookingRepository.findByStatus(status, pageable);
        } else {
            bookings = bookingRepository.findAll(pageable);
        }

        return bookings.map(bookingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingDto> findMyBookings(Pageable pageable, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return bookingRepository.findByUserId(currentUser.getId(), pageable)
                .map(bookingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findById(Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        ;
        boolean isManager = isManager(currentUser);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));

        if (!isManager && !booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to view this booking.");
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto update(Long id, CreateBookingRequestDto requestDto,
            Authentication authentication) {
        validateBookingDates(requestDto.getCheckInDate(), requestDto.getCheckOutDate());

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));

        User currentUser = (User) authentication.getPrincipal();

        if (isManager(currentUser)) {
            bookingMapper.updateBookingFromDto(requestDto, booking);

            Accommodation accommodation = accommodationRepository.findById(
                            requestDto.getAccommodationId())
                    .orElseThrow(() -> new EntityNotFoundException("Accommodation not found"));

            booking.setAccommodation(accommodation);

        } else {
            if (!booking.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You can update only your own bookings");
            }
            if (!booking.getAccommodation().getId().equals(requestDto.getAccommodationId())) {
                throw new AccessDeniedException("You cannot change accommodation");
            }
            booking.setCheckInDate(requestDto.getCheckInDate());
            booking.setCheckOutDate(requestDto.getCheckOutDate());
        }

        boolean overlaps = bookingRepository.existsByAccommodationIdAndDateOverlapExcludingBooking(
                booking.getAccommodation().getId(),
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate(),
                booking.getId()
        );

        if (overlaps) {
            throw new IllegalStateException("Accommodation is already booked for these dates.");
        }

        bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public void cancel(Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        boolean isManager = isManager(currentUser);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));

        if (!isManager && !booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to cancel this booking.");
        }

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new IllegalStateException("Booking already canceled.");
        }

        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);

        notificationService.sendMessage(String.format(
                "❌ Бронювання скасовано!\n"
                        + "Користувач: %s\n"
                        + "Житло: %s (%s)\n"
                        + "Дата заїзду: %s\n"
                        + "Дата виїзду: %s",
                booking.getUser().getEmail(),
                booking.getAccommodation().getLocation(),
                booking.getAccommodation().getCity(),
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        ));
    }

    @Override
    public BookingDto updateStatus(Long id, UpdateBookingStatusDto requestDto) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find accommodation by id: " + id)
        );
        booking.setStatus(requestDto.getBookingStatus());
        bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    private boolean isManager(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.MANAGER);
    }

    private void validateBookingDates(LocalDate checkInDate, LocalDate checkOutDate) {
        LocalDate today = LocalDate.now();

        if (checkInDate.isBefore(today)) {
            throw new IllegalArgumentException("Check-in date must be today or in the future.");
        }

        if (!checkInDate.isBefore(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date.");
        }
    }
}
