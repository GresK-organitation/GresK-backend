package com.gresk.modules.booking.application.dto;

import java.time.Instant;

public record GanttBarResponse(String bookingId, String artistName, Instant start, Instant end, String status) {
}
