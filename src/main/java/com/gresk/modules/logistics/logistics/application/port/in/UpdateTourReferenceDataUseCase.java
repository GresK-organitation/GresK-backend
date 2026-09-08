package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.UpdateTourReferenceDataCommand;
import com.gresk.modules.logistics.domain.model.Tour;

public interface UpdateTourReferenceDataUseCase {
    Tour execute(UpdateTourReferenceDataCommand command);
}
