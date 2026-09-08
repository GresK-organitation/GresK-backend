package com.gresk.modules.finance.infrastructure.web.dto;

import java.math.BigDecimal;

public record InvoiceLineResponse(
        String description,
        int quantity,
        BigDecimal unitPriceAmount,
        BigDecimal taxRatePercentage,
        BigDecimal lineTotal
) {
}
