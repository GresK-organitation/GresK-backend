package com.gresk.modules.finance.application.command;

public record CancelInvoiceCommand(String invoiceId, String promoterId) {
}
