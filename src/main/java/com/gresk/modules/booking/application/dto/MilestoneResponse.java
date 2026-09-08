package com.gresk.modules.booking.application.dto;

import java.time.Instant;

public record MilestoneResponse(String milestoneId, String type, String title, int offsetDays, Instant dueDate,
                                 String status, Instant completedAt, String notes) {
}
