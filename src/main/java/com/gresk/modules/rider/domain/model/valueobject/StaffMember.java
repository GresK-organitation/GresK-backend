package com.gresk.modules.rider.domain.model.valueobject;

public record StaffMember(String role, String name) {

    public StaffMember {
        if (role == null || role.isBlank()) throw new IllegalArgumentException("StaffMember role cannot be blank");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("StaffMember name cannot be blank");
    }
}
