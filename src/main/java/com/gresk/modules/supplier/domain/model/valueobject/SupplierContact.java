package com.gresk.modules.supplier.domain.model.valueobject;

public record SupplierContact(String contactName, String email, String phone) {

    public SupplierContact {
        if (contactName == null || contactName.isBlank())
            throw new IllegalArgumentException("SupplierContact contactName cannot be blank");
    }
}
