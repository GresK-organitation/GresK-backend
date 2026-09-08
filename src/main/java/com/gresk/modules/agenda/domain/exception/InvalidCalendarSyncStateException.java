package com.gresk.modules.agenda.domain.exception;

public class InvalidCalendarSyncStateException extends RuntimeException {
    public InvalidCalendarSyncStateException(String message) {
        super(message);
    }
}
