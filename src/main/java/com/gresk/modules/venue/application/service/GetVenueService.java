package com.gresk.modules.venue.application.service;

import com.gresk.modules.venue.application.port.in.GetVenueUseCase;
import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.port.out.VenueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetVenueService implements GetVenueUseCase {

    private final VenueRepositoryPort venueRepository;

    @Override
    public Venue execute(String venueId, String promoterId) {
        return VenueLookup.findOwned(venueRepository, venueId, promoterId);
    }
}
