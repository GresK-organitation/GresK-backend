package com.gresk.modules.rider.domain.exception;

public class ChecklistAlreadyExistsException extends RuntimeException {
    public ChecklistAlreadyExistsException(String eventId) {
        super("A checklist already exists for event: " + eventId);
    }
}
