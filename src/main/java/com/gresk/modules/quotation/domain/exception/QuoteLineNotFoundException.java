package com.gresk.modules.quotation.domain.exception;

import java.util.UUID;

public class QuoteLineNotFoundException extends RuntimeException {
    public QuoteLineNotFoundException(UUID lineId) {
        super("Quote line not found: " + lineId);
    }
}
