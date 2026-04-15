package com.gresk.modules.promoter.application.dto;

import java.math.BigDecimal;
import java.util.Set;

public record PromoterDashboardDTO(
        String name,
        String logoUrl,
        String description,
        String street,
        String city,
        String country,
        Set<String> musicalGenres,
        // ── Stats agregadas ──────────────────────────────────────────────────
        BigDecimal totalRevenue,
        long totalEvents,
        double averageRating,
        long totalAttendees,
        double sellThrough,
        long activeEvents,
        long pendingEvents,
        BigDecimal avgTicketPrice
) {}
