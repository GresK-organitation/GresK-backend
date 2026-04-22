package com.gresk.modules.event.application.usecase;

import org.springframework.web.multipart.MultipartFile;

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
        // imagen (Cloudinary public_id)
        MultipartFile coverImageFile,
        // artista (UUID del Artist del promotor; null si no aplica)
        String     artistId
) {}
