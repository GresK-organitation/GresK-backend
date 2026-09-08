package com.gresk.modules.contract.domain.model;

import java.util.UUID;

public record ContractTemplateId(UUID value) {

    public ContractTemplateId {
        if (value == null) throw new IllegalArgumentException("ContractTemplateId cannot be null");
    }

    public static ContractTemplateId generate() {
        return new ContractTemplateId(UUID.randomUUID());
    }

    public static ContractTemplateId of(String value) {
        try {
            return new ContractTemplateId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ContractTemplateId format: " + value, e);
        }
    }

    public static ContractTemplateId of(UUID value) {
        return new ContractTemplateId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
