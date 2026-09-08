package com.gresk.modules.finance.application.command;

import java.math.BigDecimal;

public record InvoiceLineData(
        String description,
        int quantity,
        BigDecimal unitPriceAmount,
        BigDecimal taxRatePercentage
) {
}
