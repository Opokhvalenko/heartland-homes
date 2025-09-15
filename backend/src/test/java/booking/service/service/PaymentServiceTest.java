package booking.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import booking.service.dto.payment.CreatePaymentRequestDto;
import booking.service.dto.payment.PaymentDto;
import booking.service.dto.payment.PaymentResponseDto;
import booking.service.exception.EntityNotFoundException;
import booking.service.exception.PaymentAlreadyExistsException;
import booking.service.mapper.PaymentMapper;
import booking.service.model.Accommodation;
import booking.service.model.Booking;
import booking.service.model.Payment;
import booking.service.model.PaymentStatus;
import booking.service.model.PaymentType;
import booking.service.model.User;
import booking.service.repository.booking.BookingRepository;
import booking.service.repository.payment.PaymentRepository;
import booking.service.service.impl.PaymentServiceImpl;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private StripeService stripeService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private Booking booking;
    private Accommodation accommodation;
    private UriComponentsBuilder uriBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentServiceImpl(
                paymentRepository, bookingRepository,
                paymentMapper, stripeService, notificationService
        );

        // тестові дані
        user = new User().setId(1L);
        accommodation = new Accommodation()
                .setId(100L)
                .setDailyRate(BigDecimal.valueOf(200));
        booking = new Booking()
                .setId(2L)
                .setUser(user)
                .setAccommodation(accommodation)
                .setCheckInDate(LocalDate.now())
                .setCheckOutDate(LocalDate.now().plusDays(2)); // 3 дні

        uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
    }

    @Test
    void createPayment_SuccessForCustomer() {
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto()
                .setBookingId(2L)
                .setPaymentType(PaymentType.PAYMENT);

        when(authentication.getPrincipal()).thenReturn(user);
        when(bookingRepository.findById(2L)).thenReturn(Optional.of(booking));
        when(paymentRepository.existsByBookingIdAndPaymentTypeAndStatus(
                2L, PaymentType.PAYMENT, PaymentStatus.PAID)).thenReturn(false);

        PaymentResponseDto stripeResponse = new PaymentResponseDto("sess_1", "url");
        when(stripeService.createStripeSession(eq(booking), eq(PaymentType.PAYMENT),
                any(), eq(uriBuilder))).thenReturn(stripeResponse);

        Payment savedPayment = new Payment().setId(10L).setBooking(booking);
        when(paymentRepository.save(any())).thenReturn(savedPayment);

        PaymentDto expectedDto = new PaymentDto()
                .setId(10L)
                .setBookingId(2L)
                .setPaymentType(PaymentType.PAYMENT)
                .setStatus(PaymentStatus.PENDING)
                .setSessionId("sess_1")
                .setSessionUrl("url");

        when(paymentMapper.toDto(any())).thenReturn(expectedDto);

        PaymentDto result = paymentService.createPayment(requestDto, authentication, uriBuilder);

        assertThat(result).isEqualTo(expectedDto);
        verify(paymentRepository).save(any());
    }

    @Test
    void createPayment_ThrowsIfAlreadyPaid() {
        when(authentication.getPrincipal()).thenReturn(user);
        when(bookingRepository.findById(2L)).thenReturn(Optional.of(booking));
        when(paymentRepository.existsByBookingIdAndPaymentTypeAndStatus(
                2L, PaymentType.PAYMENT, PaymentStatus.PAID)).thenReturn(true);

        assertThatThrownBy(() ->
                paymentService.createPayment(new CreatePaymentRequestDto()
                                .setBookingId(2L).setPaymentType(PaymentType.PAYMENT),
                        authentication, uriBuilder))
                .isInstanceOf(PaymentAlreadyExistsException.class);
    }

    @Test
    void createPayment_ThrowsIfAccessDenied() {
        User anotherUser = new User().setId(99L);
        booking.setUser(anotherUser);

        when(authentication.getPrincipal()).thenReturn(user);
        when(bookingRepository.findById(2L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() ->
                paymentService.createPayment(new CreatePaymentRequestDto()
                                .setBookingId(2L).setPaymentType(PaymentType.PAYMENT),
                        authentication, uriBuilder))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getPayments_ReturnsPaymentsForCustomer() {
        when(authentication.getPrincipal()).thenReturn(user);
        Pageable pageable = PageRequest.of(0, 10);

        when(bookingRepository.findByUserId(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(paymentRepository.findAllByBookingIdIn(List.of(2L), pageable))
                .thenReturn(new PageImpl<>(List.of(new Payment().setId(5L))));
        when(paymentMapper.toDto(any())).thenReturn(new PaymentDto().setId(5L));

        Page<PaymentDto> result = paymentService.getPayments(null, authentication, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(5L);
    }

    @Test
    void markPaymentCompleted_UpdatesStatusAndSendsNotification() {
        Payment payment = new Payment()
                .setStatus(PaymentStatus.PENDING)
                .setAmountToPay(BigDecimal.TEN)
                .setBooking(booking)
                .setSessionId("sess_123")
                .setPaymentType(PaymentType.PAYMENT);

        when(paymentRepository.findBySessionId("sess_123")).thenReturn(Optional.of(payment));
        Session session = new Session();
        session.setStatus("complete");
        when(stripeService.retrieveSession("sess_123")).thenReturn(session);

        paymentService.markPaymentCompleted("sess_123");

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
        verify(notificationService).sendMessage(anyString());
    }

    @Test
    void markPaymentCompleted_NoUpdateIfAlreadyPaid() {
        Payment payment = new Payment()
                .setStatus(PaymentStatus.PAID)
                .setBooking(booking)
                .setSessionId("sess_1");

        when(paymentRepository.findBySessionId("sess_1")).thenReturn(Optional.of(payment));

        Session session = new Session();
        session.setStatus("complete");
        when(stripeService.retrieveSession("sess_1")).thenReturn(session);

        paymentService.markPaymentCompleted("sess_1");

        verify(paymentRepository, never()).save(any());
        verify(notificationService, never()).sendMessage(any());
    }

    @Test
    void markPaymentCompleted_ThrowsIfPaymentNotFound() {
        when(paymentRepository.findBySessionId("sess_x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.markPaymentCompleted("sess_x"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}

