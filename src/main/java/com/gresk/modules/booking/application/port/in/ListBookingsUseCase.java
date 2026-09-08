package com.gresk.modules.booking.application.port.in;

import com.gresk.modules.booking.application.query.ListBookingsQuery;
import com.gresk.modules.booking.domain.model.Booking;

import java.util.List;

public interface ListBookingsUseCase {
    List<Booking> execute(ListBookingsQuery query);
}
