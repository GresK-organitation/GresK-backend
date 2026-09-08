package com.gresk.modules.venue.domain.port.out;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.model.VenueId;

import java.util.List;
import java.util.Optional;

public interface VenueRepositoryPort {
    Venue save(Venue venue);
    Optional<Venue> findById(VenueId id);
    List<Venue> findByOwner(PromoterId ownerId);
}
