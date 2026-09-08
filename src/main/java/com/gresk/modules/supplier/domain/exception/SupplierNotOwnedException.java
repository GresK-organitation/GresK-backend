package com.gresk.modules.supplier.domain.exception;

public class SupplierNotOwnedException extends RuntimeException {
    public SupplierNotOwnedException(String supplierId) {
        super("Supplier " + supplierId + " does not belong to the requesting promoter");
    }
}
