package com.gresk.modules.rider.domain.exception;

import java.util.UUID;

public class LineItemNotFoundException extends RuntimeException {
    public LineItemNotFoundException(UUID lineItemId) {
        super("Rider line item not found: " + lineItemId);
    }
}
