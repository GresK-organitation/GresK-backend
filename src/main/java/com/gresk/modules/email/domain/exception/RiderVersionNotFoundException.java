package com.gresk.modules.email.domain.exception;

import com.gresk.modules.email.domain.model.EmailRiderVersionId;

public class RiderVersionNotFoundException extends RuntimeException {
    public RiderVersionNotFoundException(EmailRiderVersionId id) {
        super("Rider version not found: " + id);
    }
}
