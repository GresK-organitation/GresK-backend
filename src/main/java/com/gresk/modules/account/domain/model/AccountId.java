package com.gresk.modules.account.domain.model;

import java.util.UUID;

public record AccountId(UUID value) {

    public AccountId {
        if (value == null) throw new IllegalArgumentException("AccountId cannot be null");
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }
    public static AccountId of(UUID value) {
        return new AccountId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
