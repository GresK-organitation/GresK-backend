package com.gresk.modules.booking.application.port.in;

import com.gresk.modules.booking.application.command.RescheduleBookingCommand;
import com.gresk.modules.booking.domain.model.Booking;

public interface RescheduleBookingUseCase {
    Booking execute(RescheduleBookingCommand command);
}
