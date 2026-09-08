package com.gresk.modules.agenda.domain.exception;

public class CalendarSyncNotConnectedException extends RuntimeException {
    public CalendarSyncNotConnectedException(String id) {
        super("Calendar sync account is not connected: " + id);
    }
}
