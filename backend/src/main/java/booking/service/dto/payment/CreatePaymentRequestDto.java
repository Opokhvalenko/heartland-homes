package booking.service.dto.payment;

import booking.service.model.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreatePaymentRequestDto {

    @NotNull
    @Positive
    private Long bookingId;

    @NotNull
    private PaymentType paymentType;
}
