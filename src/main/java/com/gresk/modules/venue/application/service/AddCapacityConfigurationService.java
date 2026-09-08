package com.gresk.modules.venue.application.service;

import com.gresk.modules.venue.application.command.AddCapacityConfigurationCommand;
import com.gresk.modules.venue.application.port.in.AddCapacityConfigurationUseCase;
import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.model.valueobject.CapacityConfiguration;
import com.gresk.modules.venue.domain.port.out.VenueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddCapacityConfigurationService implements AddCapacityConfigurationUseCase {

    private final VenueRepositoryPort venueRepository;

    @Override
    public Venue execute(AddCapacityConfigurationCommand command) {
        Venue venue = VenueLookup.findOwned(venueRepository, command.venueId(), command.promoterId());
        venue.addCapacityConfiguration(new CapacityConfiguration(
                command.code(), command.label(), command.layout(), command.maxCapacity()));
        return venueRepository.save(venue);
    }
}
