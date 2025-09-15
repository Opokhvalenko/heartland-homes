package booking.service.util;

import booking.service.dto.payment.CreatePaymentRequestDto;
import booking.service.dto.payment.PaymentDto;
import booking.service.model.PaymentStatus;
import booking.service.model.PaymentType;
import java.math.BigDecimal;
import java.util.List;

public class PaymentUtil {

    public static PaymentDto createPaymentDto1(Long id) {
        return new PaymentDto()
                .setId(id)
                .setStatus(PaymentStatus.PAID)
                .setPaymentType(PaymentType.PAYMENT)
                .setBookingId(1L)
                .setAmountToPay(BigDecimal.valueOf(400.00))
                .setSessionUrl("http://example.com/success1")
                .setSessionId("sess_1");
    }

    public static PaymentDto createPaymentDto2(Long id) {
        return new PaymentDto()
                .setId(id)
                .setStatus(PaymentStatus.PENDING)
                .setPaymentType(PaymentType.PAYMENT)
                .setBookingId(2L)
                .setAmountToPay(BigDecimal.valueOf(240.00))
                .setSessionUrl("http://example.com/success1")
                .setSessionId("sess_1");
    }

    public static CreatePaymentRequestDto createPaymentRequestDto() {
        return new CreatePaymentRequestDto()
                .setBookingId(2L)
                .setPaymentType(PaymentType.PAYMENT);
    }

    public static List<PaymentDto> createListOfPayments() {
        return List.of(
                new PaymentDto()
                        .setId(1L)
                        .setStatus(PaymentStatus.PAID)
                        .setPaymentType(PaymentType.PAYMENT)
                        .setBookingId(1L)
                        .setAmountToPay(BigDecimal.valueOf(400.00))
                        .setSessionUrl("http://example.com/success1")
                        .setSessionId("sess_1"),
                new PaymentDto()
                        .setId(2L)
                        .setStatus(PaymentStatus.PENDING)
                        .setPaymentType(PaymentType.PAYMENT)
                        .setBookingId(2L)
                        .setAmountToPay(BigDecimal.valueOf(200.00))
                        .setSessionUrl("http://example.com/success2")
                        .setSessionId("sess_2"),
                new PaymentDto()
                        .setId(3L)
                        .setStatus(PaymentStatus.PAID)
                        .setPaymentType(PaymentType.PAYMENT)
                        .setBookingId(2L)
                        .setAmountToPay(BigDecimal.valueOf(50.00))
                        .setSessionUrl("http://example.com/success3")
                        .setSessionId("sess_3")
        );
    }
}
