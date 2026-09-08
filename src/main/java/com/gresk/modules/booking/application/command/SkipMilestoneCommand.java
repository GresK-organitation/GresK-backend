package com.gresk.modules.booking.application.command;

public record SkipMilestoneCommand(String bookingId, String milestoneId, String promoterId, String reason) {
}
