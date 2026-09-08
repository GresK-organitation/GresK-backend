package com.gresk.modules.booking.application.command;

public record CompleteMilestoneCommand(String bookingId, String milestoneId, String promoterId, String notes) {
}
