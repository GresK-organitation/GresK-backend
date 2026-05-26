package com.gresk.modules.event.application.usecase;

public record UpdateFlashDealCommand(
        String  eventId,
        String  promoterId,
        boolean flashDealEnabled,
        Integer flashDealHoursThreshold,
        Integer flashDealDiscountPercent
) {}
