package com.gresk.modules.quotation.infrastructure.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record EventQuoteResponse(
        String id,
        String eventId,
        String promoterId,
        String currency,
        String status,
        List<QuoteLineResponse> lines,
        BigDecimal totalEstimatedCost,
        Instant createdAt,
        Instant updatedAt
) {}
