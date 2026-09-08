package com.gresk.modules.contract.domain.model;

import java.util.UUID;

public record AuditTrailEntryId(UUID value) {

    public AuditTrailEntryId {
        if (value == null) throw new IllegalArgumentException("AuditTrailEntryId cannot be null");
    }

    public static AuditTrailEntryId generate() {
        return new AuditTrailEntryId(UUID.randomUUID());
    }

    public static AuditTrailEntryId of(String value) {
        try {
            return new AuditTrailEntryId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid AuditTrailEntryId format: " + value, e);
        }
    }

    public static AuditTrailEntryId of(UUID value) {
        return new AuditTrailEntryId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
