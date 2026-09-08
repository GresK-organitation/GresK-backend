package com.gresk.modules.finance.application.command;

public record CancelInstallmentCommand(String installmentId, String promoterId) {
}
