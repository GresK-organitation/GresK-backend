package com.gresk.modules.show.infrastructure.web.dto;

import com.gresk.modules.show.domain.model.valueobject.LogEntryType;

import java.time.Instant;
import java.util.List;

public record ShowLogEntryResponse(
        String       id,
        String       showId,
        LogEntryType type,
        String       actor,
        Instant      occurredAt,
        String       description,
        String       relatedParty,
        List<String> attachments
) {}
