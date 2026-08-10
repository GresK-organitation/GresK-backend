package com.gresk.modules.email.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Resumen de comunicaciones de un evento para el panel B2B. */
public record EventEmailSummary(
        UUID eventId,
        String eventName,
        int emailCount,
        Instant lastEmailAt,
        int pendingActions,
        Map<String, Integer> classificationCounts,
        Map<String, String> keyEntities,
        Integer riderVersion,
        Instant riderLastUpdated,
        List<String> riderPendingItems,
        int pendingDrafts
) {}
