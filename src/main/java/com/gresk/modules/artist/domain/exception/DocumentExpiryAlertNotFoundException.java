package com.gresk.modules.artist.domain.exception;

public class DocumentExpiryAlertNotFoundException extends RuntimeException {
    public DocumentExpiryAlertNotFoundException(String alertId) {
        super("Document expiry alert not found: " + alertId);
    }
}
