package com.gresk.modules.booking.application.query;

import java.time.Instant;

public record VenueGanttQuery(String promoterId, Instant from, Instant to, String venueId) {
}
