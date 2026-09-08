package com.gresk.modules.logistics.domain.port.out;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Puerto anti-corrupción que desacopla logistics del módulo booking. Su implementación
 * en infraestructura consulta el repositorio JPA de booking directamente (infra-a-infra),
 * nunca el aggregate Booking desde el dominio. Usado para componer el Tour Book con los
 * horarios de prueba de sonido/puertas ya registrados en el day sheet de cada show.
 */
public interface BookingLogisticsQueryPort {

    /** Resolución en bloque (evita N+1 al componer el Tour Book de un Tour multi-fecha). */
    Map<UUID, BookingLogisticsView> findViews(Set<UUID> bookingIds);

    record BookingLogisticsView(UUID bookingId, String venueName, String venueCity, Instant eventDate,
                                 LocalDate daySheetShowDate, List<DaySheetLineView> daySheetLines) {
    }

    record DaySheetLineView(LocalTime time, String type, String label, String notes) {
    }
}
