package com.gresk.modules.booking.application.port.in;

import com.gresk.modules.booking.application.query.VenueGanttQuery;
import com.gresk.modules.booking.application.query.VenueGanttRow;

import java.util.List;

public interface GetVenueGanttUseCase {
    List<VenueGanttRow> execute(VenueGanttQuery query);
}
