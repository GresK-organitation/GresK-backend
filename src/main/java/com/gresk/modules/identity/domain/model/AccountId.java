package com.gresk.modules.identity.domain.model;

import java.util.UUID;

public record AccountId(UUID value) {

    public AccountId {
        if (value == null) throw new IllegalArgumentException("AccountId cannot be null");
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }

    public static AccountId of(String value) {
        try {
            return new AccountId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid AccountId format: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
