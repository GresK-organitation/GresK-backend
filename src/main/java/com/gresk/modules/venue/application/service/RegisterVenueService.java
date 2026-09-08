package com.gresk.modules.venue.application.service;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.venue.application.command.RegisterVenueCommand;
import com.gresk.modules.venue.application.port.in.RegisterVenueUseCase;
import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.port.out.VenueRepositoryPort;
import com.gresk.shared.domain.valueobject.Address;
import com.gresk.shared.domain.valueobject.City;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterVenueService implements RegisterVenueUseCase {

    private final VenueRepositoryPort venueRepository;

    @Override
    public Venue execute(RegisterVenueCommand command) {
        Address address = new Address(command.street(), City.of(command.city()), command.country());
        Venue venue = Venue.register(PromoterId.of(command.promoterId()), command.name(), address);
        return venueRepository.save(venue);
    }
}
