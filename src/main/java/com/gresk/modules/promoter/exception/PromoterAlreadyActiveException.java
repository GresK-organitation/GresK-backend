package com.gresk.modules.promoter.exception;

public class PromoterAlreadyActiveException extends RuntimeException {
    public PromoterAlreadyActiveException(String message) {
        super(message);
    }
}
