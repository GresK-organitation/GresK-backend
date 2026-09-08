package com.gresk.modules.booking.application.port.in;

import com.gresk.modules.booking.application.query.GetBookingQuery;
import com.gresk.modules.booking.domain.model.Booking;

public interface GetBookingUseCase {
    Booking execute(GetBookingQuery query);
}
