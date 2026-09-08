package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.UpdateTourDetailsCommand;
import com.gresk.modules.logistics.domain.model.Tour;

public interface UpdateTourDetailsUseCase {
    Tour execute(UpdateTourDetailsCommand command);
}
