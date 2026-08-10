package com.gresk.modules.contract.domain.model;

import java.util.UUID;

public record ContractId(UUID value) {

    public ContractId {
        if (value == null) throw new IllegalArgumentException("ContractId cannot be null");
    }

    public static ContractId generate() {
        return new ContractId(UUID.randomUUID());
    }

    public static ContractId of(String value) {
        try {
            return new ContractId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ContractId format: " + value, e);
        }
    }

    public static ContractId of(UUID value) {
        return new ContractId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
