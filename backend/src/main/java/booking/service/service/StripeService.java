package booking.service.service;

import booking.service.dto.payment.PaymentResponseDto;
import booking.service.model.Booking;
import booking.service.model.PaymentType;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import org.springframework.web.util.UriComponentsBuilder;

public interface StripeService {

    PaymentResponseDto createStripeSession(Booking booking, PaymentType type, BigDecimal amount,
            UriComponentsBuilder uriBuilder);

    Session retrieveSession(String sessionId);
}
