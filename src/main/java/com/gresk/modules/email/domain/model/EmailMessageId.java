package com.gresk.modules.email.domain.model;

import java.util.Objects;
import java.util.UUID;

public record EmailMessageId(UUID value) {

    public EmailMessageId {
        Objects.requireNonNull(value, "EmailMessageId value must not be null");
    }

    public static EmailMessageId generate() {
        return new EmailMessageId(UUID.randomUUID());
    }

    public static EmailMessageId of(String value) {
        try {
            return new EmailMessageId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EmailMessageId format: " + value, e);
        }
    }

    public static EmailMessageId of(UUID value) {
        return new EmailMessageId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
