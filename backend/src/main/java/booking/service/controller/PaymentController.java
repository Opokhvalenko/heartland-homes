package booking.service.controller;

import booking.service.dto.payment.CreatePaymentRequestDto;
import booking.service.dto.payment.PaymentDto;
import booking.service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Payments Controller", description = "Managing payment sessions for rentals")
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @PostMapping
    @Operation(summary = "Create payment session",
            description = "Creates a payment session for the given rental and returns session info")
    public PaymentDto createPayment(
            @RequestBody @Valid CreatePaymentRequestDto requestDto,
            Authentication authentication,
            UriComponentsBuilder uriBuilder
    ) {
        return paymentService.createPayment(requestDto, authentication, uriBuilder);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @GetMapping
    @Operation(summary = "Get payments by user ID",
            description = "Returns payments filtered by userId")
    public Page<PaymentDto> getPayments(
            @RequestParam(required = false, name = "user_id") Long userId,
            Authentication authentication, Pageable pageable
    ) {
        return paymentService.getPayments(userId, authentication, pageable);
    }

    @PermitAll
    @GetMapping("/success")
    @Operation(summary = "Handle successful Stripe payment",
            description = "Handles redirect from Stripe when payment succeeds")
    public ResponseEntity<Void> handleStripeSuccess(
            @RequestParam(value = "session_id", required = false) String sessionId
    ) {
        paymentService.markPaymentCompleted(sessionId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(frontendBaseUrl + "/my-payments"))
                .build();
    }

    @PermitAll
    @GetMapping("/cancel")
    @Operation(summary = "Handle canceled Stripe payment",
            description = "Informs the user that the payment was canceled")
    public ResponseEntity<Void> handleStripeCancel() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(frontendBaseUrl + "/my-bookings"))
                .build();
    }
}
