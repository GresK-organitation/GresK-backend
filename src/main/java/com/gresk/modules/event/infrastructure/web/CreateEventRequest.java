package com.gresk.modules.event.infrastructure.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateEventRequest(
        @NotBlank String  title,
        @NotNull  String  genre,            // nombre del enum MusicGenre

        // precio
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotBlank String  currency,

        // aforo
        @NotNull @Min(1) Integer totalCapacity,

        // fechas (ISO-8601 con zona horaria)
        @NotNull Instant eventDate,
        Instant          revealAt,          // opcional

        // ubicación
        @NotBlank String  street,
        @NotBlank String  city,
        @NotBlank String  country,
        String            venue,            // opcional
        @NotNull Double   latitude,
        @NotNull Double   longitude,

        // artista (opcional — la imagen se sube como multipart)
        String            artistName
) {}
