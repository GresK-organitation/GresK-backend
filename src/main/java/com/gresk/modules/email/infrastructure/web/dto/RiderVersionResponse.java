package com.gresk.modules.email.infrastructure.web.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record RiderVersionResponse(
        UUID id,
        UUID eventId,
        int versionNumber,
        UUID sourceEmailId,
        JsonNode riderData,
        JsonNode diffFromPrev,
        String createdBy,
        String notes,
        Instant createdAt
) {}
