package com.gresk.modules.event.application.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record EventResponse(
        String     id,
        String     title,
        String     promoterId,
        String     status,
        // género
        String     genre,
        // precio original y precio con descuento (null si no hay descuento)
        BigDecimal amount,
        BigDecimal discountedAmount,
        String     currency,
        // aforo
        Integer    totalCapacity,
        Integer    availableCapacity,
        // fechas (Instant — timezone-safe)
        Instant    eventDate,
        Instant    revealAt,
        Instant    createdAt,
        // ubicación
        String     street,
        String     city,
        String     country,
        String     venue,
        Double     latitude,
        Double     longitude,
        // imagen de portada
        String     coverImageUrl,
        // artista (id para referencias + datos resueltos para display)
        String     artistId,
        String     artistName,
        String     artistImageUrl
) {}
