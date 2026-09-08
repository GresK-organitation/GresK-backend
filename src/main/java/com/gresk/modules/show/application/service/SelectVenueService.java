package com.gresk.modules.show.application.service;

import com.gresk.modules.show.application.command.SelectVenueCommand;
import com.gresk.modules.show.application.port.in.SelectVenueUseCase;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.valueobject.VenueBooking;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import com.gresk.modules.show.domain.port.out.VenueTechnicalFileQueryPort;
import com.gresk.modules.venue.domain.exception.CapacityConfigurationNotFoundException;
import com.gresk.modules.venue.domain.exception.VenueNotFoundException;
import com.gresk.modules.venue.domain.model.VenueId;
import com.gresk.modules.venue.domain.model.valueobject.CapacityConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SelectVenueService implements SelectVenueUseCase {

    private final ShowRepositoryPort showRepository;
    private final VenueTechnicalFileQueryPort venueTechnicalFileQueryPort;

    @Override
    public Show execute(SelectVenueCommand command) {
        Show show = ShowLookup.findOwned(showRepository, command.showId(), command.promoterId());

        VenueTechnicalFileQueryPort.VenueSummary venueSummary = venueTechnicalFileQueryPort
                .findVenueSummary(VenueId.of(command.venueId()).value())
                .orElseThrow(() -> new VenueNotFoundException(command.venueId()));

        CapacityConfiguration config = venueTechnicalFileQueryPort
                .findCapacityConfiguration(venueSummary.venueId(), command.capacityConfigCode())
                .orElseThrow(() -> new CapacityConfigurationNotFoundException(command.capacityConfigCode()));

        show.selectVenue(new VenueBooking(
                new com.gresk.modules.venue.domain.model.VenueId(venueSummary.venueId()),
                venueSummary.name(), config.code(), config.label(), config.maxCapacity()));

        return showRepository.save(show);
    }
}
