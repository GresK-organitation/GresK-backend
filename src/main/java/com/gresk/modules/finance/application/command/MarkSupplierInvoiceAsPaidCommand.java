package com.gresk.modules.finance.application.command;

public record MarkSupplierInvoiceAsPaidCommand(String supplierInvoiceId, String promoterId) {
}
