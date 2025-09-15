package booking.service.dto.accommodation;

import booking.service.model.AccommodationStatus;
import booking.service.model.AccommodationType;
import java.math.BigDecimal;

public record AccommodationSearchParametersDto(
        String[] city,
        String[] accommodationSize,
        AccommodationType[] type,
        AccommodationStatus[] accommodationStatus,
        BigDecimal minDailyRate,
        BigDecimal maxDailyRate
) {

}
