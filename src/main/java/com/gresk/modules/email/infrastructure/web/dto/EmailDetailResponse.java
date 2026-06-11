package com.gresk.modules.email.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record EmailDetailResponse(
        UUID id,
        UUID eventId,
        String messageIdExternal,
        String threadIdExternal,
        String fromAddress,
        String fromName,
        List<String> toAddresses,
        String subject,
        String bodyText,
        String bodyHtml,
        String classification,
        BigDecimal classificationConfidence,
        String processingStatus,
        Instant receivedAt,
        Instant processedAt
) {}
