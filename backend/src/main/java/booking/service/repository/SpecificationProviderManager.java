package booking.service.repository;

public interface SpecificationProviderManager<T> {
    <P> SpecificationProvider<T, P> getSpecificationProvider(String key, Class<P> paramType);
}
