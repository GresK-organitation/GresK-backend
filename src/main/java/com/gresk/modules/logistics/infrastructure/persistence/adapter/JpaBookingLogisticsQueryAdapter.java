package com.gresk.modules.logistics.infrastructure.persistence.adapter;

import com.gresk.modules.booking.infrastructure.persistence.entity.BookingDaySheetEntryEntity;
import com.gresk.modules.booking.infrastructure.persistence.entity.BookingEntity;
import com.gresk.modules.booking.infrastructure.persistence.repository.BookingJpaRepository;
import com.gresk.modules.logistics.domain.port.out.BookingLogisticsQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Puerto anti-corrupción hacia el módulo booking: consulta su repositorio JPA
 * directamente en infraestructura, nunca el aggregate Booking desde el dominio.
 * Usado para traer el day sheet (horarios de prueba de sonido/puertas) de cada show
 * al compilar el Tour Book.
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaBookingLogisticsQueryAdapter implements BookingLogisticsQueryPort {

    private final BookingJpaRepository bookingJpaRepository;

    @Override
    public Map<UUID, BookingLogisticsView> findViews(Set<UUID> bookingIds) {
        if (bookingIds == null || bookingIds.isEmpty()) return Map.of();
        return bookingJpaRepository.findAllById(bookingIds).stream()
                .collect(Collectors.toMap(BookingEntity::getId, this::toView));
    }

    private BookingLogisticsView toView(BookingEntity entity) {
        var lines = entity.getDaySheetEntries().stream()
                .sorted(Comparator.comparingInt(BookingDaySheetEntryEntity::getSortOrder))
                .map(e -> new DaySheetLineView(e.getEntryTime(), e.getType(), e.getLabel(), e.getNotes()))
                .toList();
        return new BookingLogisticsView(entity.getId(), entity.getVenueName(), entity.getVenueCity(),
                entity.getEventDate(), entity.getDaySheetShowDate(), lines);
    }
}
