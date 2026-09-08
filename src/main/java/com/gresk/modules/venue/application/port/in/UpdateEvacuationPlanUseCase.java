package com.gresk.modules.venue.application.port.in;

import com.gresk.modules.venue.application.command.UpdateEvacuationPlanCommand;
import com.gresk.modules.venue.domain.model.Venue;

public interface UpdateEvacuationPlanUseCase {
    Venue execute(UpdateEvacuationPlanCommand command);
}
