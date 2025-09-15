package booking.service.repository.accommodation;

import booking.service.exception.SpecificationProviderManagerException;
import booking.service.model.Accommodation;
import booking.service.repository.SpecificationProvider;
import booking.service.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccommodationSpecificationProviderManager
        implements SpecificationProviderManager<Accommodation> {

    private final List<SpecificationProvider<Accommodation, ?>> providers;

    @SuppressWarnings("unchecked")
    public <P> SpecificationProvider<Accommodation, P> getSpecificationProvider(
            String key,
            Class<P> paramType) {
        return (SpecificationProvider<Accommodation, P>) providers.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new SpecificationProviderManagerException(
                        "Can't find correct specification provider for key " + key
                ));
    }
}
