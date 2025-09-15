package booking.service.repository.accommodation.spec;

import booking.service.model.Accommodation;
import booking.service.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TypeSpecificationProvider implements SpecificationProvider<Accommodation, String[]> {

    private static final String TYPE = "type";

    @Override
    public String getKey() {
        return TYPE;
    }

    @Override
    public Specification<Accommodation> getSpecification(String[] params) {
        return (root, query, cb)
                -> root.get(TYPE).in((Object[]) params);
    }
}
