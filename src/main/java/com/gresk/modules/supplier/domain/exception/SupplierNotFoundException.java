package com.gresk.modules.supplier.domain.exception;

public class SupplierNotFoundException extends RuntimeException {
    public SupplierNotFoundException(String supplierId) {
        super("Supplier not found: " + supplierId);
    }
}
