package com.gresk.modules.venue.application.port.in;

import com.gresk.modules.venue.application.command.AddCapacityConfigurationCommand;
import com.gresk.modules.venue.domain.model.Venue;

public interface AddCapacityConfigurationUseCase {
    Venue execute(AddCapacityConfigurationCommand command);
}
