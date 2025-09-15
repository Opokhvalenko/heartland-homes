package booking.service.dto.booking;

import booking.service.model.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateBookingStatusDto {
    @NotNull
    private BookingStatus bookingStatus;
}
