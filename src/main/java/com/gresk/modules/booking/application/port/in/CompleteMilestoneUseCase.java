package com.gresk.modules.booking.application.port.in;

import com.gresk.modules.booking.application.command.CompleteMilestoneCommand;
import com.gresk.modules.booking.domain.model.Booking;

public interface CompleteMilestoneUseCase {
    Booking execute(CompleteMilestoneCommand command);
}
