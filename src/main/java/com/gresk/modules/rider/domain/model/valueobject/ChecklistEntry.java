package com.gresk.modules.rider.domain.model.valueobject;

import com.gresk.modules.rider.domain.model.BacklineCategory;

import java.time.Instant;
import java.util.UUID;

public record ChecklistEntry(
        UUID entryId,
        BacklineCategory category,
        String description,
        boolean required,
        boolean confirmed,
        Instant confirmedAt,
        String confirmedNotes
) {
    public ChecklistEntry {
        if (entryId == null) entryId = UUID.randomUUID();
        if (category == null) throw new IllegalArgumentException("ChecklistEntry category cannot be null");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("ChecklistEntry description cannot be blank");
    }

    public ChecklistEntry confirm(String notes) {
        return new ChecklistEntry(entryId, category, description, required, true, Instant.now(), notes);
    }

    public ChecklistEntry unconfirm() {
        return new ChecklistEntry(entryId, category, description, required, false, null, null);
    }
}
