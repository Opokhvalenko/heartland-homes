package booking.service.service.impl;

import booking.service.dto.payment.CreatePaymentRequestDto;
import booking.service.dto.payment.PaymentDto;
import booking.service.dto.payment.PaymentResponseDto;
import booking.service.exception.EntityNotFoundException;
import booking.service.exception.PaymentAlreadyExistsException;
import booking.service.mapper.PaymentMapper;
import booking.service.model.Booking;
import booking.service.model.Payment;
import booking.service.model.PaymentStatus;
import booking.service.model.PaymentType;
import booking.service.model.RoleName;
import booking.service.model.User;
import booking.service.repository.booking.BookingRepository;
import booking.service.repository.payment.PaymentRepository;
import booking.service.service.NotificationService;
import booking.service.service.PaymentService;
import booking.service.service.StripeService;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PaymentDto createPayment(CreatePaymentRequestDto requestDto,
            Authentication authentication,
            UriComponentsBuilder uriBuilder) {
        User currentUser = (User) authentication.getPrincipal();

        Booking booking = bookingRepository.findById(requestDto.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: "
                        + requestDto.getBookingId()));

        boolean isManager = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.MANAGER);

        if (!isManager && !booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to create a booking for another user's.");
        }

        if (requestDto.getPaymentType() == PaymentType.PAYMENT) {
            boolean alreadyPaid = paymentRepository.existsByBookingIdAndPaymentTypeAndStatus(
                    booking.getId(),
                    PaymentType.PAYMENT,
                    PaymentStatus.PAID
            );

            if (alreadyPaid) {
                throw new PaymentAlreadyExistsException("This booking has already been paid.");
            }
        }

        BigDecimal amountToPay = calculateAmountToPay(booking, requestDto.getPaymentType());

        PaymentResponseDto stripeSession = stripeService.createStripeSession(
                booking,
                requestDto.getPaymentType(),
                amountToPay,
                uriBuilder
        );

        Payment payment = new Payment()
                .setBooking(booking)
                .setStatus(PaymentStatus.PENDING)
                .setPaymentType(requestDto.getPaymentType())
                .setAmountToPay(amountToPay)
                .setSessionId(stripeSession.sessionId())
                .setSessionUrl(stripeSession.sessionUrl());

        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Override
    public Page<PaymentDto> getPayments(Long userId, Authentication authentication,
            Pageable pageable) {
        User currentUser = (User) authentication.getPrincipal();

        boolean isManager = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.MANAGER);

        if (!isManager && userId != null && !userId.equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to view payments of another user.");
        }

        Long actualUserId = isManager ? userId : currentUser.getId();

        Page<Payment> payments;

        if (actualUserId != null) {
            Page<Booking> bookings = bookingRepository.findByUserId(actualUserId, pageable);

            List<Long> bookingIds = bookings.stream()
                    .map(Booking::getId)
                    .toList();

            if (bookingIds.isEmpty()) {
                return Page.empty(pageable);
            }

            payments = paymentRepository.findAllByBookingIdIn(bookingIds, pageable);
        } else {
            payments = paymentRepository.findAll(pageable);
        }

        return payments.map(paymentMapper::toDto);
    }

    @Override
    @Transactional
    public void markPaymentCompleted(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found by session ID"));
        Session session = stripeService.retrieveSession(sessionId);

        if ("complete".equals(session.getStatus()) && payment.getStatus() != PaymentStatus.PAID) {
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);

            String message = String.format("""
                            ✅ Новий платіж виконано!
                            
                            💳 Сума: %s грн
                            👤 ID користувача: %s
                            📦 ID житла: %s
                            🧾 Тип оплати: %s
                            🔁 ID сесії: %s
                            """,
                    payment.getAmountToPay(),
                    payment.getBooking().getUser().getId(),
                    payment.getBooking().getId(),
                    payment.getPaymentType(),
                    payment.getSessionId()
            );

            notificationService.sendMessage(message);
        }
    }

    private BigDecimal calculateAmountToPay(Booking booking, PaymentType type) {
        BigDecimal dailyFee = booking.getAccommodation().getDailyRate();
        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        return dailyFee.multiply(BigDecimal.valueOf(nights));
    }
}
