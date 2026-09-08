package com.gresk.modules.booking.application.command;

import java.time.Instant;

public record RescheduleBookingCommand(String bookingId, String promoterId, Instant newEventDate, String reason) {
}
