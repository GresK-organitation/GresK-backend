package com.gresk.modules.promoter.domain.exception;

public class PromoterNotFoundException extends RuntimeException {
    public PromoterNotFoundException(String promoterId) {
        super("Promoter not found: " + promoterId);
    }
}
