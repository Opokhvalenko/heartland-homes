package booking.service.repository.accommodation.spec;

import booking.service.model.Accommodation;
import booking.service.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AccommodationStatusSpecificationProvider implements
        SpecificationProvider<Accommodation, String[]> {

    private static final String ACCOMMODATION_STATUS = "accommodationStatus";

    @Override
    public String getKey() {
        return ACCOMMODATION_STATUS;
    }

    @Override
    public Specification<Accommodation> getSpecification(String[] params) {
        return (root, query, cb)
                -> root.get(ACCOMMODATION_STATUS).in((Object[]) params);
    }
}
