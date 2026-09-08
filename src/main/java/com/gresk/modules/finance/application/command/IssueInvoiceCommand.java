package com.gresk.modules.finance.application.command;

import java.time.LocalDate;
import java.util.List;

public record IssueInvoiceCommand(
        String promoterId,
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
