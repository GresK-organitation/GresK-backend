package com.gresk.modules.booking.domain.port.out;

import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.BookingId;
import com.gresk.modules.booking.domain.model.BookingStatus;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepositoryPort {

    Booking save(Booking booking);

    Optional<Booking> findById(BookingId id);

    List<Booking> findActiveByArtistId(UUID artistId, PromoterId promoterId);

    List<Booking> findByPromoterAndDateRange(PromoterId promoterId, Instant from, Instant to);

    List<Booking> findByVenueAndDateRange(PromoterId promoterId, UUID venueId, Instant from, Instant to);

    /** Holds (HOLD_1/HOLD_2) cuyo {@code holdExpiresAt} ya pasó. */
    List<Booking> findExpirable(Instant now);

    List<Booking> findByPromoter(PromoterId promoterId, Instant from, Instant to, BookingStatus status);
}
