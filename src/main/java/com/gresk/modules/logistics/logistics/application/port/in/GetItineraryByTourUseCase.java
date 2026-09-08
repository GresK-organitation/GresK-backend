package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.query.GetItineraryByTourQuery;
import com.gresk.modules.logistics.domain.model.Itinerary;

public interface GetItineraryByTourUseCase {
    Itinerary execute(GetItineraryByTourQuery query);
}
