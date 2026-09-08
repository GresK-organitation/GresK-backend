package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.CreateTravelPartyCommand;
import com.gresk.modules.logistics.domain.model.TravelParty;

public interface CreateTravelPartyUseCase {
    TravelParty execute(CreateTravelPartyCommand command);
}
