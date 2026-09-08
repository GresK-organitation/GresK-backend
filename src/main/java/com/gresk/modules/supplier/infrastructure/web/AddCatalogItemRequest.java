package com.gresk.modules.supplier.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AddCatalogItemRequest(
        @NotBlank String category,
        @NotBlank String itemName,
        @NotNull @Positive BigDecimal unitPriceAmount,
        @NotBlank String unitPriceCurrency,
        @NotBlank String pricingUnit,
        Integer leadTimeDays,
        String notes
) {}
