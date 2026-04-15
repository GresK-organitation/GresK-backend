package com.gresk.modules.promoter.application.dto;

import java.math.BigDecimal;

public record PromoterEventDTO(
        String id,
        String title,
        String eventDate,       // ISO-8601, ej: "2026-04-12T22:00:00Z"
        String venue,
        String city,
        String status,          // DRAFT | PUBLISHED | FINISHED | CANCELLED
        int totalCapacity,
        int ticketsSold,        // totalCapacity - availableCapacity
        BigDecimal revenue,     // ticketsSold × precio efectivo
        BigDecimal price,       // precio efectivo (discountedAmount ?? amount)
        String genre,
        String coverImageUrl
) {}
