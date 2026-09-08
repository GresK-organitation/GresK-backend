package com.gresk.modules.logistics.domain.model.valueobject;

public record EmergencyContact(String name, String role, String phone, String notes) {

    public EmergencyContact {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("EmergencyContact name must not be blank");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("EmergencyContact phone must not be blank");
        }
    }
}
