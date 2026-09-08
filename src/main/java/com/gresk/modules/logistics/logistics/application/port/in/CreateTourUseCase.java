package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.CreateTourCommand;
import com.gresk.modules.logistics.domain.model.Tour;

public interface CreateTourUseCase {
    Tour execute(CreateTourCommand command);
}
