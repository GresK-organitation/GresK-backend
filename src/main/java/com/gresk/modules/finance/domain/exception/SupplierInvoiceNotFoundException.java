package com.gresk.modules.finance.domain.exception;

public class SupplierInvoiceNotFoundException extends RuntimeException {
    public SupplierInvoiceNotFoundException(String id) {
        super("Supplier invoice not found: " + id);
    }
}
