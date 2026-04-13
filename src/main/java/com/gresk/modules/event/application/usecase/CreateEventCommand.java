package com.gresk.modules.event.application.usecase;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateEventCommand(
        String     promoterId,
        String     title,
        // género (nombre del enum MusicGenre)
        String     genre,
        // precio
        BigDecimal amount,
        String     currency,
        // aforo
        Integer    totalCapacity,
        // fechas
        Instant    eventDate,
        Instant    revealAt,
        // ubicación
        String     street,
        String     city,
        String     country,
        String     venue,
        Double     latitude,
        Double     longitude,
        // imagen
        String     coverImageUrl,
        // artista
        String     artistName,
        String     artistImageUrl
) {}
