package com.gresk.modules.booking.application.port.in;

import com.gresk.modules.booking.application.command.CancelBookingCommand;
import com.gresk.modules.booking.domain.model.Booking;

public interface CancelBookingUseCase {
    Booking execute(CancelBookingCommand command);
}
