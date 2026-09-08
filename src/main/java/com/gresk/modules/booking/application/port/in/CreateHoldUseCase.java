package com.gresk.modules.booking.application.port.in;

import com.gresk.modules.booking.application.command.CreateHoldCommand;
import com.gresk.modules.booking.domain.model.Booking;

public interface CreateHoldUseCase {
    Booking execute(CreateHoldCommand command);
}
