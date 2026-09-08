package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.CreateItineraryCommand;
import com.gresk.modules.logistics.domain.model.Itinerary;

public interface CreateItineraryUseCase {
    Itinerary execute(CreateItineraryCommand command);
}
