package com.gresk.modules.finance.domain.model;

import java.util.UUID;

public record SettlementAgreementId(UUID value) {

    public SettlementAgreementId {
        if (value == null) throw new IllegalArgumentException("SettlementAgreementId cannot be null");
    }

    public static SettlementAgreementId generate() {
        return new SettlementAgreementId(UUID.randomUUID());
    }

    public static SettlementAgreementId of(String value) {
        try {
            return new SettlementAgreementId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid SettlementAgreementId format: " + value, e);
        }
    }

    public static SettlementAgreementId of(UUID value) {
        return new SettlementAgreementId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
