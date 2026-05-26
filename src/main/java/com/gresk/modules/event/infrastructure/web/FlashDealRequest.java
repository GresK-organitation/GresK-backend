package com.gresk.modules.event.infrastructure.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Cuerpo de la petición para crear o actualizar la configuración del flash deal.
 * Usado en: PUT /api/v1/events/{id}/flash-deal
 */
public record FlashDealRequest(
        boolean             flashDealEnabled,
        @Min(1)             Integer flashDealHoursThreshold,
        @Min(1) @Max(99)    Integer flashDealDiscountPercent
) {}
