package com.gresk.modules.show.infrastructure.venue;

import com.gresk.modules.show.domain.port.out.VenueTechnicalFileQueryPort;
import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.model.VenueId;
import com.gresk.modules.venue.domain.model.valueobject.CapacityConfiguration;
import com.gresk.modules.venue.domain.model.valueobject.LicenseType;
import com.gresk.modules.venue.domain.port.out.VenueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador anti-corrupción: {@code show} solo ve {@link VenueTechnicalFileQueryPort}, nunca
 * el agregado {@code Venue} ni su repositorio directamente. Este componente es el único punto
 * de la base de código que traduce entre ambos módulos.
 */
@Component
@RequiredArgsConstructor
public class VenueTechnicalFileQueryAdapter implements VenueTechnicalFileQueryPort {

    private final VenueRepositoryPort venueRepository;

    @Override
    public Optional<VenueSummary> findVenueSummary(UUID venueId) {
        return venueRepository.findById(new VenueId(venueId))
                .map(v -> new VenueSummary(venueId, v.getName(), v.isActive()));
    }

    @Override
    public Optional<CapacityConfiguration> findCapacityConfiguration(UUID venueId, String capacityConfigCode) {
        return venueRepository.findById(new VenueId(venueId))
                .flatMap(v -> v.getCapacityConfigurations().stream()
                        .filter(c -> c.code().equalsIgnoreCase(capacityConfigCode))
                        .findFirst());
    }

    @Override
    public boolean hasValidLicenseOn(UUID venueId, LocalDate date) {
        Optional<Venue> venue = venueRepository.findById(new VenueId(venueId));
        return venue.flatMap(v -> v.licenseOf(LicenseType.SPECTACLE_PUBLIC))
                .map(license -> license.isValidOn(date))
                .orElse(false);
    }
}
