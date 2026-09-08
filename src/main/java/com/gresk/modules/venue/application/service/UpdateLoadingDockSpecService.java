package com.gresk.modules.venue.application.service;

import com.gresk.modules.venue.application.command.UpdateLoadingDockSpecCommand;
import com.gresk.modules.venue.application.port.in.UpdateLoadingDockSpecUseCase;
import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.model.valueobject.LoadingDockSpec;
import com.gresk.modules.venue.domain.port.out.VenueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateLoadingDockSpecService implements UpdateLoadingDockSpecUseCase {

    private final VenueRepositoryPort venueRepository;

    @Override
    public Venue execute(UpdateLoadingDockSpecCommand command) {
        Venue venue = VenueLookup.findOwned(venueRepository, command.venueId(), command.promoterId());
        venue.updateLoadingDockSpec(new LoadingDockSpec(
                command.accessHeightMeters(), command.accessWidthMeters(), command.maxVehicleWeightKg(),
                command.windowStart(), command.windowEnd(), command.dockCount(), command.notes()));
        return venueRepository.save(venue);
    }
}
