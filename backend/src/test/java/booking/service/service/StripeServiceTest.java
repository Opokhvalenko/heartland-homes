package booking.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import booking.service.dto.payment.PaymentResponseDto;
import booking.service.model.Booking;
import booking.service.model.PaymentType;
import booking.service.service.impl.StripeServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    private StripeServiceImpl stripeService;

    @BeforeEach
    void setUp() {
        stripeService = new StripeServiceImpl();
        ReflectionTestUtils.setField(stripeService, "currency", "usd");
    }

    @Test
    void createStripeSession_success() {
        Booking booking = new Booking();
        PaymentType type = PaymentType.PAYMENT;
        BigDecimal amountToPay = BigDecimal.valueOf(100);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        Session fakeSession = new Session();
        fakeSession.setId("sess_123");
        fakeSession.setUrl("http://stripe.checkout/sess_123");

        try (MockedStatic<Session> sessionMockedStatic = mockStatic(Session.class)) {
            sessionMockedStatic.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(fakeSession);

            PaymentResponseDto result = stripeService.createStripeSession(booking,
                    type, amountToPay, uriBuilder);

            assertThat(result).isNotNull();
            assertThat(result.sessionId()).isEqualTo("sess_123");
            assertThat(result.sessionUrl()).isEqualTo("http://stripe.checkout/sess_123");

            sessionMockedStatic.verify(() -> Session.create(any(SessionCreateParams.class)),
                    times(1));
        }
    }

    @Test
    void createStripeSession_failure_throwsRuntimeException() {
        Booking booking = new Booking();
        PaymentType type = PaymentType.PAYMENT;
        BigDecimal amountToPay = BigDecimal.valueOf(50);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        StripeException stripeException = mock(StripeException.class);

        try (MockedStatic<Session> sessionMockedStatic = mockStatic(Session.class)) {
            sessionMockedStatic.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenThrow(stripeException);

            assertThatThrownBy(
                    () -> stripeService.createStripeSession(booking, type, amountToPay, uriBuilder))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to create Stripe session");

            sessionMockedStatic.verify(() -> Session.create(any(SessionCreateParams.class)),
                    times(1));
        }
    }

    @Test
    void retrieveSession_success() throws StripeException {
        Session fakeSession = new Session();
        fakeSession.setId("sess_456");

        try (MockedStatic<Session> sessionMockedStatic = mockStatic(Session.class)) {
            sessionMockedStatic.when(() -> Session.retrieve("sess_456"))
                    .thenReturn(fakeSession);

            Session result = stripeService.retrieveSession("sess_456");

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("sess_456");

            sessionMockedStatic.verify(() -> Session.retrieve("sess_456"), times(1));
        }
    }

    @Test
    void createStripeSession_throwsException() {
        Booking booking = new Booking();
        PaymentType type = PaymentType.PAYMENT;
        BigDecimal amountToPay = BigDecimal.valueOf(100);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        StripeException stripeException = mock(StripeException.class);

        try (MockedStatic<Session> sessionMockedStatic = mockStatic(Session.class)) {
            sessionMockedStatic.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenThrow(stripeException);

            assertThatThrownBy(
                    () -> stripeService.createStripeSession(booking, type, amountToPay, uriBuilder))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to create Stripe session");
        }
    }
}
