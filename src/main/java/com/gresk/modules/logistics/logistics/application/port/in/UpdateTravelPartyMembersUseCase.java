package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.UpdateTravelPartyMembersCommand;
import com.gresk.modules.logistics.domain.model.TravelParty;

public interface UpdateTravelPartyMembersUseCase {
    TravelParty execute(UpdateTravelPartyMembersCommand command);
}
