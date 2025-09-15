package booking.service.dto.accommodation;

import booking.service.model.AccommodationStatus;
import booking.service.model.AccommodationType;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AccommodationDto {
    private Long id;
    private String name;
    private AccommodationType type;
    private String location;
    private String city;
    private String latitude;
    private String longitude;
    private String size;
    private List<String> amenities;
    private BigDecimal dailyRate;
    private String image;
    private AccommodationStatus accommodationStatus;
    private String ownerEmail;
}
