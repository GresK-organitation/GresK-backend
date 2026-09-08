package com.gresk.modules.artist.infrastructure.web;

import java.time.Instant;
import java.time.LocalDate;

public record DocumentExpiryAlertResponse(
        String id,
        String promoterId,
        String artistId,
        String bandMemberId,
        String documentType,
        LocalDate expiryDate,
        String message,
        boolean read,
        Instant createdAt
) {}
