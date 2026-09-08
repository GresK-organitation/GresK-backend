package com.gresk.modules.booking.application.port.in;

import com.gresk.modules.booking.application.command.PromoteHoldCommand;
import com.gresk.modules.booking.domain.model.Booking;

public interface PromoteHoldUseCase {
    Booking execute(PromoteHoldCommand command);
}
