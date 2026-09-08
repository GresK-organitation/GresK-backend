package com.gresk.modules.booking.application.query;

import java.util.List;

public record VenueGanttRow(String venueId, String venueName, List<GanttBar> bars) {
}
