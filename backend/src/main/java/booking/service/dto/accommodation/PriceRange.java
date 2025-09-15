package booking.service.dto.accommodation;

import java.math.BigDecimal;

public record PriceRange(BigDecimal min, BigDecimal max) {
}
