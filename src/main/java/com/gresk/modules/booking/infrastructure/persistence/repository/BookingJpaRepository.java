package com.gresk.modules.booking.infrastructure.persistence.repository;

import com.gresk.modules.booking.infrastructure.persistence.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, UUID> {

    @Query("SELECT b FROM BookingEntity b WHERE b.artistId = :artistId AND b.promoterId = :promoterId "
            + "AND b.status IN ('HOLD_1','HOLD_2','CONFIRMED')")
    List<BookingEntity> findActiveByArtistAndPromoter(@Param("artistId") UUID artistId, @Param("promoterId") UUID promoterId);

    @Query("SELECT b FROM BookingEntity b WHERE b.promoterId = :promoterId AND b.eventDate BETWEEN :from AND :to")
    List<BookingEntity> findByPromoterAndDateRange(@Param("promoterId") UUID promoterId,
                                                    @Param("from") Instant from, @Param("to") Instant to);

    @Query("SELECT b FROM BookingEntity b WHERE b.promoterId = :promoterId AND b.venueId = :venueId "
            + "AND b.eventDate BETWEEN :from AND :to")
    List<BookingEntity> findByVenueAndDateRange(@Param("promoterId") UUID promoterId, @Param("venueId") UUID venueId,
                                                 @Param("from") Instant from, @Param("to") Instant to);

    @Query("SELECT b FROM BookingEntity b WHERE b.status IN ('HOLD_1','HOLD_2') AND b.holdExpiresAt <= :now")
    List<BookingEntity> findExpirable(@Param("now") Instant now);

    @Query("SELECT b FROM BookingEntity b WHERE b.promoterId = :promoterId AND b.eventDate BETWEEN :from AND :to "
            + "AND (:status IS NULL OR b.status = :status)")
    List<BookingEntity> findByPromoter(@Param("promoterId") UUID promoterId, @Param("from") Instant from,
                                        @Param("to") Instant to, @Param("status") String status);
}
