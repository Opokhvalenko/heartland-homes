package booking.service.repository.accommodation.spec;

import booking.service.model.Accommodation;
import booking.service.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SizeSpecificationProvider implements SpecificationProvider<Accommodation, String[]> {

    private static final String SIZE = "accommodationSize";

    @Override
    public String getKey() {
        return SIZE;
    }

    @Override
    public Specification<Accommodation> getSpecification(String[] params) {
        return (root, query, cb)
                -> root.get(SIZE).in((Object[]) params);
    }
}
