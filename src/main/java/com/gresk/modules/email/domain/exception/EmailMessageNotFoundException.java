package com.gresk.modules.email.domain.exception;

import com.gresk.modules.email.domain.model.EmailMessageId;

public class EmailMessageNotFoundException extends RuntimeException {
    public EmailMessageNotFoundException(EmailMessageId id) {
        super("Email message not found: " + id);
    }
}
