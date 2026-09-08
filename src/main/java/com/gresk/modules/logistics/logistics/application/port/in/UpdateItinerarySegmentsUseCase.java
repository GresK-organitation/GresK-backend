package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.UpdateItinerarySegmentsCommand;
import com.gresk.modules.logistics.domain.model.Itinerary;

public interface UpdateItinerarySegmentsUseCase {
    Itinerary execute(UpdateItinerarySegmentsCommand command);
}
