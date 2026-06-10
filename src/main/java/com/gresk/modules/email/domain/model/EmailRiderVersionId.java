package com.gresk.modules.email.domain.model;

import java.util.Objects;
import java.util.UUID;

public record EmailRiderVersionId(UUID value) {

    public EmailRiderVersionId {
        Objects.requireNonNull(value, "EmailRiderVersionId value must not be null");
    }

    public static EmailRiderVersionId generate() {
        return new EmailRiderVersionId(UUID.randomUUID());
    }

    public static EmailRiderVersionId of(String value) {
        try {
            return new EmailRiderVersionId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EmailRiderVersionId format: " + value, e);
        }
    }

    public static EmailRiderVersionId of(UUID value) {
        return new EmailRiderVersionId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
