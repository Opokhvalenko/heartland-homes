package booking.service.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import booking.service.model.Accommodation;
import booking.service.repository.accommodation.AccommodationRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccommodationRepositoryTest {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Test
    @Sql(scripts = {
            "classpath:database/delete-users-and-roles.sql",
            "classpath:database/insert-users-and-roles.sql",
            "classpath:database/delete-accommodations.sql",
            "classpath:database/insert-accommodations.sql",
            "classpath:database/delete-bookings.sql",
            "classpath:database/insert-bookings.sql",
            "classpath:database/delete-payments.sql",
            "classpath:database/insert-payments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/delete-payments.sql",
            "classpath:database/delete-bookings.sql",
            "classpath:database/delete-accommodations.sql",
            "classpath:database/delete-users-and-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_shouldReturnInsertedAccommodations() {
        List<Accommodation> accommodations = accommodationRepository.findAll();
        assertEquals(4, accommodations.size());
    }
}


