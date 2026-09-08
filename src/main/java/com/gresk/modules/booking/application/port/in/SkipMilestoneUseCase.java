package com.gresk.modules.booking.application.port.in;

import com.gresk.modules.booking.application.command.SkipMilestoneCommand;
import com.gresk.modules.booking.domain.model.Booking;

public interface SkipMilestoneUseCase {
    Booking execute(SkipMilestoneCommand command);
}
