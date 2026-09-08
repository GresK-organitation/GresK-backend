package com.gresk.modules.booking.application.dto;

import java.time.Instant;

public record TerritorialConflictResponse(String conflictingBookingId, String venueName, Instant eventDate) {
}
