package com.gresk.modules.supplier.application.command;

import java.math.BigDecimal;

public record AddSupplierCatalogItemCommand(
        String supplierId,
        String promoterId,
        String category,
        String itemName,
        BigDecimal unitPriceAmount,
        String unitPriceCurrency,
        String pricingUnit,
        Integer leadTimeDays,
        String notes
) {}
