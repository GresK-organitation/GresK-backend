package com.gresk.modules.artist.domain.model.valueobject;

import java.util.UUID;

public record DocumentExpiryAlertId(UUID value) {

    public DocumentExpiryAlertId {
        if (value == null) throw new IllegalArgumentException("DocumentExpiryAlertId cannot be null");
    }

    public static DocumentExpiryAlertId generate() { return new DocumentExpiryAlertId(UUID.randomUUID()); }

    public static DocumentExpiryAlertId of(String value) {
        try {
            return new DocumentExpiryAlertId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid DocumentExpiryAlertId format: " + value, e);
        }
    }

    public static DocumentExpiryAlertId of(UUID value) { return new DocumentExpiryAlertId(value); }

    @Override
    public String toString() { return value.toString(); }
}
