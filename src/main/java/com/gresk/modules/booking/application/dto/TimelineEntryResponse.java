package com.gresk.modules.booking.application.dto;

import java.time.Instant;

public record TimelineEntryResponse(String bookingId, String venueName, Instant eventDate, String status,
                                     int pendingMilestones, String nextMilestoneTitle) {
}
