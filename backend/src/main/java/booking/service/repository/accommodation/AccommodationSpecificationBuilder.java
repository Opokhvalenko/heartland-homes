package booking.service.repository.accommodation;

import booking.service.dto.accommodation.AccommodationSearchParametersDto;
import booking.service.dto.accommodation.PriceRange;
import booking.service.model.Accommodation;
import booking.service.repository.SpecificationBuilder;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccommodationSpecificationBuilder implements SpecificationBuilder<Accommodation> {

    private static final String CITY = "city";
    private static final String SIZE = "accommodationSize";
    private static final String TYPE = "type";
    private static final String DAILY_RATE = "dailyRate";
    private static final String ACCOMMODATION_STATUS = "accommodationStatus";

    private final AccommodationSpecificationProviderManager providerManager;

    @Override
    public Specification<Accommodation> build(AccommodationSearchParametersDto searchParameters) {
        Specification<Accommodation> spec = Specification.where(null);

        if (searchParameters.city() != null && searchParameters.city().length > 0) {
            spec = spec.and(providerManager
                    .getSpecificationProvider(CITY, String[].class)
                    .getSpecification(searchParameters.city()));
        }

        if (searchParameters.accommodationSize() != null
                && searchParameters.accommodationSize().length > 0) {
            spec = spec.and(providerManager
                    .getSpecificationProvider(SIZE, String[].class)
                    .getSpecification(searchParameters.accommodationSize()));
        }

        if (searchParameters.type() != null && searchParameters.type().length > 0) {
            String[] typeNames = Arrays.stream(searchParameters.type())
                    .map(Enum::name)
                    .toArray(String[]::new);

            spec = spec.and(providerManager
                    .getSpecificationProvider(TYPE, String[].class)
                    .getSpecification(typeNames));
        }

        if (searchParameters.accommodationStatus() != null
                && searchParameters.accommodationStatus().length > 0) {
            String[] statusNames = Arrays.stream(searchParameters.accommodationStatus())
                    .map(Enum::name)
                    .toArray(String[]::new);

            spec = spec.and(providerManager
                    .getSpecificationProvider(ACCOMMODATION_STATUS, String[].class)
                    .getSpecification(statusNames));
        }

        if (searchParameters.minDailyRate() != null || searchParameters.maxDailyRate() != null) {
            spec = spec.and(providerManager
                    .getSpecificationProvider(DAILY_RATE, PriceRange.class)
                    .getSpecification(new PriceRange(
                            searchParameters.minDailyRate(),
                            searchParameters.maxDailyRate()
                    )));
        }

        return spec;
    }
}
