package booking.service.repository.booking;

import booking.service.model.Booking;
import booking.service.model.BookingStatus;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    Page<Booking> findByUserIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    @Query("""
           SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
           FROM Booking b
           WHERE b.accommodation.id = :accommodationId
             AND b.status <> 'CANCELED'
             AND b.checkInDate < :checkOutDate
             AND b.checkOutDate > :checkInDate
           \s""")
    boolean existsByAccommodationIdAndDateOverlap(
            @Param("accommodationId") Long accommodationId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    @Query("""
         SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
         FROM Booking b
         WHERE b.accommodation.id = :accommodationId
            AND b.id <> :bookingId
            AND b.status <> 'CANCELED'
            AND b.checkInDate < :checkOutDate
            AND b.checkOutDate > :checkInDate
          \s""")
    boolean existsByAccommodationIdAndDateOverlapExcludingBooking(
            @Param("accommodationId") Long accommodationId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("bookingId") Long bookingId
    );
}
