package com.gresk.modules.venue.application.service;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.venue.domain.exception.VenueNotFoundException;
import com.gresk.modules.venue.domain.exception.VenueNotOwnedException;
import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.model.VenueId;
import com.gresk.modules.venue.domain.port.out.VenueRepositoryPort;

/** Helper interno: evita repetir el par find-or-404 + check-ownership-or-403 en cada servicio. */
final class VenueLookup {

    private VenueLookup() {
    }

    static Venue findOwned(VenueRepositoryPort repository, String venueId, String promoterId) {
        Venue venue = repository.findById(VenueId.of(venueId))
                .orElseThrow(() -> new VenueNotFoundException(venueId));
        if (!venue.getOwnerId().equals(PromoterId.of(promoterId))) {
            throw new VenueNotOwnedException();
        }
        return venue;
    }
}
