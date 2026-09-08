package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.ChangeTourStatusCommand;
import com.gresk.modules.logistics.domain.model.Tour;

public interface ChangeTourStatusUseCase {
    Tour execute(ChangeTourStatusCommand command);
}
