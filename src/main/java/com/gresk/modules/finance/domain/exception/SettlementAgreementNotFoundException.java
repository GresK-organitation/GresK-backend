package com.gresk.modules.finance.domain.exception;

public class SettlementAgreementNotFoundException extends RuntimeException {
    public SettlementAgreementNotFoundException(String id) {
        super("Settlement agreement not found: " + id);
    }
}
