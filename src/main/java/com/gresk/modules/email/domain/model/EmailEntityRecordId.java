package com.gresk.modules.email.domain.model;

import java.util.Objects;
import java.util.UUID;

public record EmailEntityRecordId(UUID value) {

    public EmailEntityRecordId {
        Objects.requireNonNull(value, "EmailEntityRecordId value must not be null");
    }

    public static EmailEntityRecordId generate() {
        return new EmailEntityRecordId(UUID.randomUUID());
    }

    public static EmailEntityRecordId of(String value) {
        try {
            return new EmailEntityRecordId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EmailEntityRecordId format: " + value, e);
        }
    }

    public static EmailEntityRecordId of(UUID value) {
        return new EmailEntityRecordId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
