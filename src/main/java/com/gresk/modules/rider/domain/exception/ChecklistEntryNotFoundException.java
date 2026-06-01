package com.gresk.modules.rider.domain.exception;

public class ChecklistEntryNotFoundException extends RuntimeException {
    public ChecklistEntryNotFoundException(String entryId) {
        super("Checklist entry not found: " + entryId);
    }
}
