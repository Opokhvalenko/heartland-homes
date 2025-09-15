package booking.service.repository;

import booking.service.dto.accommodation.AccommodationSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {

    Specification<T> build(AccommodationSearchParametersDto searchParameters);
}
