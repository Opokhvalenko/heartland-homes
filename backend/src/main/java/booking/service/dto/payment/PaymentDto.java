package booking.service.dto.payment;

import booking.service.model.PaymentStatus;
import booking.service.model.PaymentType;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentDto {

    private Long id;
    private PaymentStatus status;
    private PaymentType paymentType;
    private Long bookingId;
    private BigDecimal amountToPay;
    private String sessionUrl;
    private String sessionId;
}
