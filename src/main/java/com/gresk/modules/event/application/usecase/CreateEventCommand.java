package com.gresk.modules.event.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateEventCommand(
        String promoterId,
        String title,
        String genre,
        BigDecimal amount,
        String currency,
        Integer totalCapacity,
        LocalDateTime eventDate,
        String city,
        String address,
        String venue,
        LocalDateTime revealAt
) {}
