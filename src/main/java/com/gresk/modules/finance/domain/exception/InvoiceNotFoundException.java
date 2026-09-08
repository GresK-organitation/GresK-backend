package com.gresk.modules.finance.domain.exception;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(String id) {
        super("Invoice not found: " + id);
    }
}
