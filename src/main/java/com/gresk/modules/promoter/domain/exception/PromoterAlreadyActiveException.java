package com.gresk.modules.promoter.domain.exception;

public class PromoterAlreadyActiveException extends RuntimeException {
    public PromoterAlreadyActiveException(String message) {
        super(message);
    }
}
