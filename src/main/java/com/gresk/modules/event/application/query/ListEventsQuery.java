package com.gresk.modules.event.application.query;

import java.math.BigDecimal;
import java.time.Instant;

public record ListEventsQuery(
        String     genre,
        String     city,
        Instant    dateFrom,
        Instant    dateTo,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String     artistName,
        int        page,
        int        size
) {}
