package com.gresk.modules.booking.application.command;

public record CancelBookingCommand(String bookingId, String promoterId, String reason) {
}
