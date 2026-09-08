package com.gresk.modules.booking.domain.exception;

public class MilestoneAlreadyCompletedException extends RuntimeException {
    public MilestoneAlreadyCompletedException(String milestoneId) {
        super("Milestone already completed: " + milestoneId);
    }
}
