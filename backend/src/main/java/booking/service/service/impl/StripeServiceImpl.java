package booking.service.service.impl;

import booking.service.dto.payment.PaymentResponseDto;
import booking.service.model.Booking;
import booking.service.model.PaymentType;
import booking.service.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.currency:usd}")
    private String currency;

    @Override
    public PaymentResponseDto createStripeSession(Booking booking,
            PaymentType type,
            BigDecimal amountToPay,
            UriComponentsBuilder uriBuilder) {
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(uriBuilder
                            .path("/payments/success")
                            .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                            .build()
                            .toString())
                    .setCancelUrl(uriBuilder
                            .path("/payments/cancel")
                            .build()
                            .toString())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(currency)
                                                    .setUnitAmount(amountToPay
                                                            .multiply(BigDecimal.valueOf(100))
                                                            .longValue())
                                                    .setProductData(
                                                            SessionCreateParams
                                                                    .LineItem
                                                                    .PriceData
                                                                    .ProductData.builder()
                                                                    .setName("Booking rental: "
                                                                            + type.name())
                                                                    .build())
                                                    .build())
                                    .build())
                    .build();

            Session session = Session.create(params);

            return new PaymentResponseDto(session.getId(), session.getUrl());

        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Stripe session", e);
        }
    }

    @Override
    public Session retrieveSession(String sessionId) {
        try {
            return Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new RuntimeException("Failed to retrieve Stripe session", e);
        }
    }
}
