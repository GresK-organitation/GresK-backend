package com.gresk.modules.quotation.infrastructure.web;

import java.math.BigDecimal;
import java.util.UUID;

public record QuoteLineResponse(
        UUID id,
        String riderType,
        UUID riderId,
        UUID lineItemId,
        String category,
        String description,
        int quantity,
        String fulfillmentSource,
        String supplierId,
        UUID catalogItemId,
        BigDecimal unitCostAmount,
        String unitCostCurrency,
        BigDecimal subtotal
) {}
