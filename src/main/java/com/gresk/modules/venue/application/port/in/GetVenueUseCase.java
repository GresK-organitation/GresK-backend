package com.gresk.modules.venue.application.port.in;

import com.gresk.modules.venue.domain.model.Venue;

public interface GetVenueUseCase {
    Venue execute(String venueId, String promoterId);
}
