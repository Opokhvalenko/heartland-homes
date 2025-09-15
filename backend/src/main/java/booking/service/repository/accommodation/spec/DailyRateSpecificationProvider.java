package booking.service.repository.accommodation.spec;

import booking.service.dto.accommodation.PriceRange;
import booking.service.model.Accommodation;
import booking.service.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DailyRateSpecificationProvider implements
        SpecificationProvider<Accommodation, PriceRange> {

    @Override
    public String getKey() {
        return "dailyRate";
    }

    @Override
    public Specification<Accommodation> getSpecification(PriceRange params) {
        return (root, query, cb) -> {
            if (params.min() != null && params.max() != null) {
                return cb.between(root.get("dailyRate"), params.min(), params.max());
            } else if (params.min() != null) {
                return cb.greaterThanOrEqualTo(root.get("dailyRate"), params.min());
            } else if (params.max() != null) {
                return cb.lessThanOrEqualTo(root.get("dailyRate"), params.max());
            }
            return cb.conjunction();
        };
    }
}



