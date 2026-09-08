package com.gresk.modules.artist.domain.exception;

public class DocumentExpiryAlertNotOwnedException extends RuntimeException {
    public DocumentExpiryAlertNotOwnedException() {
        super("Document expiry alert does not belong to this promoter");
    }
}
