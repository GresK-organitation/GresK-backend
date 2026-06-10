package com.gresk.modules.email.domain.model;

import java.util.List;

/** Diferencias entre dos versiones de rider (claves añadidas, eliminadas y modificadas). */
public record RiderDiff(List<String> added, List<String> removed, List<FieldChange> modified) {

    public RiderDiff {
        added    = added    != null ? List.copyOf(added)    : List.of();
        removed  = removed  != null ? List.copyOf(removed)  : List.of();
        modified = modified != null ? List.copyOf(modified) : List.of();
    }

    public static RiderDiff empty() {
        return new RiderDiff(List.of(), List.of(), List.of());
    }

    public boolean hasChanges() {
        return !added.isEmpty() || !removed.isEmpty() || !modified.isEmpty();
    }

    public record FieldChange(String field, String oldValue, String newValue) {}
}
