package com.gresk.modules.account.domain.exception;

public class PromoterAlreadyActiveException extends RuntimeException {
    public PromoterAlreadyActiveException(String message) {
        super(message);
    }
}
