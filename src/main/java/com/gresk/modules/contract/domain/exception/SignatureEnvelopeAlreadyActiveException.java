package com.gresk.modules.contract.domain.exception;

public class SignatureEnvelopeAlreadyActiveException extends RuntimeException {
    public SignatureEnvelopeAlreadyActiveException() {
        super("This contract already has an active signature envelope");
    }
}
