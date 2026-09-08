package com.gresk.modules.booking.application.port.in;

import com.gresk.modules.booking.application.command.UpdateDaySheetCommand;
import com.gresk.modules.booking.domain.model.Booking;

public interface UpdateDaySheetUseCase {
    Booking execute(UpdateDaySheetCommand command);
}
