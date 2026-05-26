package com.gresk.modules.event.application.usecase;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateEventCommand(
        String     promoterId,
        String     title,
        String     genre,
        BigDecimal amount,
        String     currency,
        Integer    totalCapacity,
        Instant    eventDate,
        Instant    revealAt,
        String     street,
        String     city,
        String     country,
        String     venue,
        Double     latitude,
        Double     longitude,
        MultipartFile coverImageFile,
        String     artistId,
        // Flash Deal (opcional — null si no se configura en la creación)
        Boolean    flashDealEnabled,
        Integer    flashDealHoursThreshold,
        Integer    flashDealDiscountPercent
) {}
