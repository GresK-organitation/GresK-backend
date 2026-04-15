package com.gresk.modules.promoter.domain.model.valueobject;

import java.math.BigDecimal;

public record PromoterStats(
        BigDecimal totalRevenue,
        long totalEvents,
        double averageRating,
        long totalAttendees,
        double sellThrough,
        long activeEvents,
        long pendingEvents,
        BigDecimal avgTicketPrice
) {}