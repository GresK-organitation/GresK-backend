package com.gresk.modules.finance.application.command;

public record MarkInvoicePaidCommand(String invoiceId, String promoterId) {
}
