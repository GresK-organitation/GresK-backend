package com.gresk.modules.finance.domain.model;

import java.util.UUID;

public record PaymentInstallmentId(UUID value) {

    public PaymentInstallmentId {
        if (value == null) throw new IllegalArgumentException("PaymentInstallmentId cannot be null");
    }

    public static PaymentInstallmentId generate() {
        return new PaymentInstallmentId(UUID.randomUUID());
    }

    public static PaymentInstallmentId of(String value) {
        try {
            return new PaymentInstallmentId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid PaymentInstallmentId format: " + value, e);
        }
    }

    public static PaymentInstallmentId of(UUID value) {
        return new PaymentInstallmentId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
