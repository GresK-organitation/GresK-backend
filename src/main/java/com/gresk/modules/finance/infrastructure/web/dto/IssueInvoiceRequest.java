package com.gresk.modules.finance.infrastructure.web.dto;

import com.gresk.modules.finance.application.command.InvoiceLineData;

import java.time.LocalDate;
import java.util.List;

public record IssueInvoiceRequest(
        String linkedEventId,
        String recipientName,
        String recipientTaxId,
        String recipientAddress,
        String recipientCountry,
        String recipientEmail,
        String currency,
        List<InvoiceLineData> lines,
        LocalDate issueDate,
        LocalDate dueDate
) {
}
