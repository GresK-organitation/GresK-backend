package com.gresk.modules.show.domain.port.out;

import com.gresk.modules.venue.domain.model.valueobject.CapacityConfiguration;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de solo lectura hacia el módulo {@code venue}. {@code show} no depende del agregado
 * {@code Venue} ni de su repositorio: solo necesita consultar un aforo modular concreto y si
 * la licencia municipal cubre una fecha dada, igual que {@code booking.BookingArtistQueryPort}
 * consulta el módulo {@code artist} sin acoplarse a su dominio interno.
 */
public interface VenueTechnicalFileQueryPort {
    Optional<VenueSummary> findVenueSummary(UUID venueId);
    Optional<CapacityConfiguration> findCapacityConfiguration(UUID venueId, String capacityConfigCode);
    boolean hasValidLicenseOn(UUID venueId, LocalDate date);

    record VenueSummary(UUID venueId, String name, boolean active) {
    }
}
