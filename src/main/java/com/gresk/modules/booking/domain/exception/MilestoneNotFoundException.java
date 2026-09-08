package com.gresk.modules.booking.domain.exception;

public class MilestoneNotFoundException extends RuntimeException {
    public MilestoneNotFoundException(String milestoneId) {
        super("Milestone not found: " + milestoneId);
    }
}
