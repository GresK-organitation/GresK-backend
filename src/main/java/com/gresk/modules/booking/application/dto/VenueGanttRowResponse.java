package com.gresk.modules.booking.application.dto;

import java.util.List;

public record VenueGanttRowResponse(String venueId, String venueName, List<GanttBarResponse> bars) {
}
