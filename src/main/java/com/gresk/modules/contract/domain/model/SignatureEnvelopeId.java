package com.gresk.modules.contract.domain.model;

import java.util.UUID;

public record SignatureEnvelopeId(UUID value) {

    public SignatureEnvelopeId {
        if (value == null) throw new IllegalArgumentException("SignatureEnvelopeId cannot be null");
    }

    public static SignatureEnvelopeId generate() {
        return new SignatureEnvelopeId(UUID.randomUUID());
    }

    public static SignatureEnvelopeId of(String value) {
        try {
            return new SignatureEnvelopeId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid SignatureEnvelopeId format: " + value, e);
        }
    }

    public static SignatureEnvelopeId of(UUID value) {
        return new SignatureEnvelopeId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
