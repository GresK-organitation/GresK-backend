package com.gresk.modules.promoter.application.dto;

import java.math.BigDecimal;

public record PromoterEventDTO(
        String     id,
        String     title,
        String     eventDate,
        String     venue,
        String     city,
        String     status,
        int        totalCapacity,
        int        ticketsSold,
        BigDecimal revenue,
        BigDecimal price,
        String     genre,
        String     coverImageUrl,
        Double     conversionRate,
        double     avgRating
) {}
