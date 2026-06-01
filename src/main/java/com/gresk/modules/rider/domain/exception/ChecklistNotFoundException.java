package com.gresk.modules.rider.domain.exception;

public class ChecklistNotFoundException extends RuntimeException {
    public ChecklistNotFoundException(String eventId) {
        super("Checklist not found for event: " + eventId);
    }
}
