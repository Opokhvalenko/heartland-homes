package booking.service.dto.accommodation;

import booking.service.model.AccommodationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateAccommodationStatusDto {
    @NotNull
    private AccommodationStatus status;
}
