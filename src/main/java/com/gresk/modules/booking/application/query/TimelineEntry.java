package com.gresk.modules.booking.application.query;

import java.time.Instant;

public record TimelineEntry(String bookingId, String venueName, Instant eventDate, String status,
                             int pendingMilestones, String nextMilestoneTitle) {
}
