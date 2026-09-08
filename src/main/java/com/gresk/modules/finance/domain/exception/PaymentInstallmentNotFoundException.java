package com.gresk.modules.finance.domain.exception;

public class PaymentInstallmentNotFoundException extends RuntimeException {
    public PaymentInstallmentNotFoundException(String id) {
        super("Payment installment not found: " + id);
    }
}
