package com.gresk.modules.agenda.domain.exception;

public class UnsupportedCalendarProviderException extends RuntimeException {
    public UnsupportedCalendarProviderException(String provider) {
        super("Unsupported calendar provider: " + provider);
    }
}
