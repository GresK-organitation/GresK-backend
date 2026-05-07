package com.gresk.modules.promoter.domain.exception;

public class PromoterNotActiveException extends RuntimeException {
    public PromoterNotActiveException(String message) {
        super(message);
    }
}
