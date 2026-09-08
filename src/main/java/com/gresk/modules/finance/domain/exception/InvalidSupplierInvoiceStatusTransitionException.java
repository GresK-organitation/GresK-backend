package com.gresk.modules.finance.domain.exception;

public class InvalidSupplierInvoiceStatusTransitionException extends RuntimeException {
    public InvalidSupplierInvoiceStatusTransitionException(String message) {
        super(message);
    }
}
