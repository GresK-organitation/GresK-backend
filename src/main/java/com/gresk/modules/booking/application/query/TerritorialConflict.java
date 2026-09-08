package com.gresk.modules.booking.application.query;

import java.time.Instant;

public record TerritorialConflict(String conflictingBookingId, String venueName, Instant eventDate) {
}
