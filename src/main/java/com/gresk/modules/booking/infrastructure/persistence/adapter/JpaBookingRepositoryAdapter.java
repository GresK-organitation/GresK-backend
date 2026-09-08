package com.gresk.modules.booking.infrastructure.persistence.adapter;

import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.BookingId;
import com.gresk.modules.booking.domain.model.BookingStatus;
import com.gresk.modules.booking.domain.port.out.BookingRepositoryPort;
import com.gresk.modules.booking.infrastructure.persistence.mapper.BookingMapper;
import com.gresk.modules.booking.infrastructure.persistence.repository.BookingJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaBookingRepositoryAdapter implements BookingRepositoryPort {

    private final BookingJpaRepository jpaRepository;
    private final BookingMapper mapper;

    @Override
    @Transactional
    public Booking save(Booking booking) {
        var saved = jpaRepository.save(mapper.toEntity(booking));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Booking> findById(BookingId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Booking> findActiveByArtistId(UUID artistId, PromoterId promoterId) {
        return jpaRepository.findActiveByArtistAndPromoter(artistId, promoterId.value()).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<Booking> findByPromoterAndDateRange(PromoterId promoterId, Instant from, Instant to) {
        return jpaRepository.findByPromoterAndDateRange(promoterId.value(), from, to).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<Booking> findByVenueAndDateRange(PromoterId promoterId, UUID venueId, Instant from, Instant to) {
        return jpaRepository.findByVenueAndDateRange(promoterId.value(), venueId, from, to).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<Booking> findExpirable(Instant now) {
        return jpaRepository.findExpirable(now).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Booking> findByPromoter(PromoterId promoterId, Instant from, Instant to, BookingStatus status) {
        return jpaRepository.findByPromoter(promoterId.value(), from, to, status == null ? null : status.name())
                .stream().map(mapper::toDomain).toList();
    }
}
