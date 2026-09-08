package com.gresk.modules.finance.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SupplierInvoiceResponse(
        String id,
        String linkedEventId,
        String linkedCostLineId,
        String supplierName,
        String supplierInvoiceNumber,
        BigDecimal amount,
        BigDecimal taxAmount,
        BigDecimal total,
        String currency,
        LocalDate issueDate,
        LocalDate dueDate,
        BigDecimal budgetedAmountSnapshot,
        BigDecimal deviationPercentage,
        boolean deviationExceedsThreshold,
        String status,
        String disputeReason
) {
}
