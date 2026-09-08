package com.gresk.modules.venue.application.service;

import com.gresk.modules.venue.application.command.UpdateCurfewPolicyCommand;
import com.gresk.modules.venue.application.port.in.UpdateCurfewPolicyUseCase;
import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.model.valueobject.CurfewPolicy;
import com.gresk.modules.venue.domain.port.out.VenueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCurfewPolicyService implements UpdateCurfewPolicyUseCase {

    private final VenueRepositoryPort venueRepository;

    @Override
    public Venue execute(UpdateCurfewPolicyCommand command) {
        Venue venue = VenueLookup.findOwned(venueRepository, command.venueId(), command.promoterId());
        venue.updateCurfewPolicy(new CurfewPolicy(
                command.hardCutoff(), command.maxDecibels(), command.restrictedDays(), command.notes()));
        return venueRepository.save(venue);
    }
}
