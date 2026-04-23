package com.gresk.modules.event.infrastructure.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateEventRequest(
        @NotBlank String  title,
        @NotNull  String  genre,

        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotBlank String  currency,

        @NotNull @Min(1) Integer totalCapacity,

        @NotNull Instant eventDate,
        Instant          revealAt,

        @NotBlank String  street,
        @NotBlank String  city,
        @NotBlank String  country,
        String            venue,
        @NotNull Double   latitude,
        @NotNull Double   longitude,

        String            artistId
) {}
