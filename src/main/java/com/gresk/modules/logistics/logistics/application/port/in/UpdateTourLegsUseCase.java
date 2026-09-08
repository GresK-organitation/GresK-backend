package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.UpdateTourLegsCommand;
import com.gresk.modules.logistics.domain.model.Tour;

public interface UpdateTourLegsUseCase {
    Tour execute(UpdateTourLegsCommand command);
}
