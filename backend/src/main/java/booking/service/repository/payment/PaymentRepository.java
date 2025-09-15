package booking.service.repository.payment;

import booking.service.model.Payment;
import booking.service.model.PaymentStatus;
import booking.service.model.PaymentType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findAllByBookingIdIn(List<Long> bookingIds, Pageable pageable);

    Optional<Payment> findBySessionId(String sessionId);

    boolean existsByBookingIdAndPaymentTypeAndStatus(
            Long bookingId,
            PaymentType paymentType,
            PaymentStatus status
    );

    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
            FROM Payment p
            JOIN p.booking b
            WHERE b.user.id = :userId
              AND p.status = 'PENDING'
              AND p.isDeleted = false
            """)
    boolean existsUnpaidPaymentsByUserId(@Param("userId") Long userId);
}
