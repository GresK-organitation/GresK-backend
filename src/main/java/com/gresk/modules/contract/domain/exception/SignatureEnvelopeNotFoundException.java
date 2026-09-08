package com.gresk.modules.contract.domain.exception;

public class SignatureEnvelopeNotFoundException extends RuntimeException {
    public SignatureEnvelopeNotFoundException(String id) {
        super("Signature envelope not found: " + id);
    }
}
