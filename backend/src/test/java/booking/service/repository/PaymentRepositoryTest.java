package booking.service.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import booking.service.model.PaymentStatus;
import booking.service.model.PaymentType;
import booking.service.repository.payment.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

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
    void existsByBookingIdAndPaymentTypeAndStatus_shouldReturnTrue() {
        boolean exists = paymentRepository.existsByBookingIdAndPaymentTypeAndStatus(
                2L,
                PaymentType.PAYMENT,
                PaymentStatus.PENDING
        );
        assertTrue(exists);
    }

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
    void findBySessionId_shouldReturnPayment() {
        var maybe = paymentRepository.findBySessionId("sess_1");
        assertTrue(maybe.isPresent());
        assertFalse(maybe.get().isDeleted());
    }
}


