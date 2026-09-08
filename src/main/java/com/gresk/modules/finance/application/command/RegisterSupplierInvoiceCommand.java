package com.gresk.modules.finance.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterSupplierInvoiceCommand(
        String promoterId,
        String linkedEventId,
        String linkedCostLineId,
        String supplierName,
        String supplierTaxId,
        String supplierAddress,
        String supplierCountry,
        String supplierEmail,
        String supplierInvoiceNumber,
        BigDecimal amount,
        BigDecimal taxAmount,
        String currency,
        LocalDate issueDate,
        LocalDate dueDate
) {
}
