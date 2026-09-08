package com.gresk.modules.booking.application.query;

import com.gresk.modules.booking.application.command.VenueRefInput;

import java.time.Instant;

public record CheckTerritorialExclusivityQuery(String promoterId, String artistId, VenueRefInput venue,
                                                 Instant eventDate, int daysBefore, int daysAfter,
                                                 String excludeBookingId) {
}
