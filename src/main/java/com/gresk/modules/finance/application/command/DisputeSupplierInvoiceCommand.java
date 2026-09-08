package com.gresk.modules.finance.application.command;

public record DisputeSupplierInvoiceCommand(String supplierInvoiceId, String promoterId, String reason) {
}
