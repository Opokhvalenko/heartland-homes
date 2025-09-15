package booking.service.mapper;

import booking.service.config.MapperConfig;
import booking.service.dto.payment.CreatePaymentRequestDto;
import booking.service.dto.payment.PaymentDto;
import booking.service.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {

    @Mapping(source = "booking.id", target = "bookingId")
    PaymentDto toDto(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true)
    Payment toEntity(CreatePaymentRequestDto requestDto);
}
