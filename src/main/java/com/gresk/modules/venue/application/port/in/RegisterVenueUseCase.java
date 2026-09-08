package com.gresk.modules.venue.application.port.in;

import com.gresk.modules.venue.application.command.RegisterVenueCommand;
import com.gresk.modules.venue.domain.model.Venue;

public interface RegisterVenueUseCase {
    Venue execute(RegisterVenueCommand command);
}
