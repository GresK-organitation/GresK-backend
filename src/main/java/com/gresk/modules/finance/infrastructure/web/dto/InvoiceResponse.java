package com.gresk.modules.finance.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceResponse(
        String id,
        String linkedEventId,
        String invoiceNumber,
        String recipientName,
        String recipientTaxId,
        List<InvoiceLineResponse> lines,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal total,
        String currency,
        String status,
        LocalDate issueDate,
        LocalDate dueDate,
        String pdfAssetId
) {
}
