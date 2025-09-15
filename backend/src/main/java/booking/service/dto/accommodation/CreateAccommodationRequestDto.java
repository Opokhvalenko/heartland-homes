package booking.service.dto.accommodation;

import booking.service.model.AccommodationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateAccommodationRequestDto {

    @NotNull
    private String name;

    @NotNull
    private AccommodationType type;

    @NotBlank
    private String location;

    @NotBlank
    private String city;

    @NotBlank
    private String latitude;

    @NotBlank
    private String longitude;

    @NotBlank
    private String size;

    @NotNull
    private List<@NotBlank String> amenities;

    @NotNull
    @Positive
    private BigDecimal dailyRate;

    @NotNull
    private String image;
}
