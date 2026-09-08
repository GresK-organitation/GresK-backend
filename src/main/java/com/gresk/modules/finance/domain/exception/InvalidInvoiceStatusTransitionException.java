package com.gresk.modules.finance.domain.exception;

public class InvalidInvoiceStatusTransitionException extends RuntimeException {
    public InvalidInvoiceStatusTransitionException(String message) {
        super(message);
    }
}
