package com.gresk.modules.venue.application.service;

import com.gresk.modules.venue.application.command.UpdateEvacuationPlanCommand;
import com.gresk.modules.venue.application.port.in.UpdateEvacuationPlanUseCase;
import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.model.valueobject.EvacuationPlan;
import com.gresk.modules.venue.domain.port.out.VenueRepositoryPort;
import com.gresk.shared.domain.valueobject.AssetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateEvacuationPlanService implements UpdateEvacuationPlanUseCase {

    private final VenueRepositoryPort venueRepository;

    @Override
    public Venue execute(UpdateEvacuationPlanCommand command) {
        Venue venue = VenueLookup.findOwned(venueRepository, command.venueId(), command.promoterId());
        venue.updateEvacuationPlan(new EvacuationPlan(
                AssetId.of(command.documentAssetId()), command.certifiedCapacity(),
                command.lastReviewedAt(), command.reviewedBy()));
        return venueRepository.save(venue);
    }
}
