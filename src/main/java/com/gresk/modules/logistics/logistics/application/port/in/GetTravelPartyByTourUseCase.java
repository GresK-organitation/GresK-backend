package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.query.GetTravelPartyByTourQuery;
import com.gresk.modules.logistics.domain.model.TravelParty;

public interface GetTravelPartyByTourUseCase {
    TravelParty execute(GetTravelPartyByTourQuery query);
}
