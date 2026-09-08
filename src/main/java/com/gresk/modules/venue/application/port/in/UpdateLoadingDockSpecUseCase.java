package com.gresk.modules.venue.application.port.in;

import com.gresk.modules.venue.application.command.UpdateLoadingDockSpecCommand;
import com.gresk.modules.venue.domain.model.Venue;

public interface UpdateLoadingDockSpecUseCase {
    Venue execute(UpdateLoadingDockSpecCommand command);
}
