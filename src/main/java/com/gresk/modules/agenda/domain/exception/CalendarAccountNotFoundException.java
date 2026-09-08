package com.gresk.modules.agenda.domain.exception;

public class CalendarAccountNotFoundException extends RuntimeException {
    public CalendarAccountNotFoundException(String id) {
        super("Calendar sync account not found: " + id);
    }
}
