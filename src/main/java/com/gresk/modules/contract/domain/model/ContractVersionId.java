package com.gresk.modules.contract.domain.model;

import java.util.UUID;

public record ContractVersionId(UUID value) {

    public ContractVersionId {
        if (value == null) throw new IllegalArgumentException("ContractVersionId cannot be null");
    }

    public static ContractVersionId generate() {
        return new ContractVersionId(UUID.randomUUID());
    }

    public static ContractVersionId of(String value) {
        try {
            return new ContractVersionId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ContractVersionId format: " + value, e);
        }
    }

    public static ContractVersionId of(UUID value) {
        return new ContractVersionId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
