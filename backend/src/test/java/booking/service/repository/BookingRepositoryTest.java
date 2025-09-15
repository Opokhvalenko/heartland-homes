package booking.service.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import booking.service.model.Booking;
import booking.service.model.BookingStatus;
import booking.service.repository.booking.BookingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @Sql(scripts = {
            "classpath:database/delete-users-and-roles.sql",
            "classpath:database/insert-users-and-roles.sql",
            "classpath:database/delete-accommodations.sql",
            "classpath:database/insert-accommodations.sql",
            "classpath:database/delete-bookings.sql",
            "classpath:database/insert-bookings.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/delete-bookings.sql",
            "classpath:database/delete-accommodations.sql",
            "classpath:database/delete-users-and-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserIdAndStatus_shouldReturnPage() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Booking> page =
                bookingRepository.findByUserIdAndStatus(2L, BookingStatus.CONFIRMED, pageable);
        assertEquals(1, page.getTotalElements());
        assertEquals(BookingStatus.CONFIRMED, page.getContent().get(0).getStatus());
    }

    @Test
    @Sql(scripts = {
            "classpath:database/delete-users-and-roles.sql",
            "classpath:database/insert-users-and-roles.sql",
            "classpath:database/delete-accommodations.sql",
            "classpath:database/insert-accommodations.sql",
            "classpath:database/delete-bookings.sql",
            "classpath:database/insert-bookings.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/delete-bookings.sql",
            "classpath:database/delete-accommodations.sql",
            "classpath:database/delete-users-and-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void existsByAccommodationIdAndDateOverlap_shouldReturnTrue() {
        boolean exists = bookingRepository.existsByAccommodationIdAndDateOverlap(1L,
                java.time.LocalDate.parse("2025-09-02"),
                java.time.LocalDate.parse("2025-09-03"));
        assertTrue(exists);
    }
}


