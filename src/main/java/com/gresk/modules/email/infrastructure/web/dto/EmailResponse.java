package com.gresk.modules.email.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record EmailResponse(
        UUID id,
        UUID eventId,
        String threadIdExternal,
        String fromAddress,
        String fromName,
        String subject,
        String classification,
        BigDecimal classificationConfidence,
        String processingStatus,
        Instant receivedAt,
        Instant processedAt
) {}
