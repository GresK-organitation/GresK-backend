package com.gresk.modules.rider.infrastructure.web;

import java.time.Instant;
import java.util.UUID;

public record ChecklistEntryResponse(
        UUID entryId,
        String category,
        String description,
        boolean required,
        boolean confirmed,
        Instant confirmedAt,
        String confirmedNotes
) {}
