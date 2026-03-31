package com.gresk.modules.event.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(
        String id,
        String title,
        String promoterId,
        String status,
        String genre,
        BigDecimal amount,
        String currency,
        Integer totalCapacity,
        Integer availableCapacity,
        LocalDateTime eventDate,
        String city,
        String address,
        String venue,
        LocalDateTime revealAt,
        LocalDateTime createdAt
) {}
