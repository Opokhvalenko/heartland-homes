package booking.service.service;

import booking.service.dto.payment.CreatePaymentRequestDto;
import booking.service.dto.payment.PaymentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.util.UriComponentsBuilder;

public interface PaymentService {

    PaymentDto createPayment(CreatePaymentRequestDto requestDto, Authentication authentication,
            UriComponentsBuilder uriComponentsBuilder);

    Page<PaymentDto> getPayments(Long userId, Authentication authentication, Pageable pageable);

    void markPaymentCompleted(String sessionId);
}
