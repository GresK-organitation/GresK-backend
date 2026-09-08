package com.gresk.modules.supplier.infrastructure.web;

import java.math.BigDecimal;
import java.util.UUID;

public record CatalogItemResponse(
        UUID id,
        String category,
        String itemName,
        BigDecimal unitPriceAmount,
        String unitPriceCurrency,
        String pricingUnit,
        Integer leadTimeDays,
        boolean active,
        String notes
) {}
