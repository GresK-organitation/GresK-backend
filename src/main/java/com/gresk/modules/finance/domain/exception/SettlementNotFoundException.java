package com.gresk.modules.finance.domain.exception;

public class SettlementNotFoundException extends RuntimeException {
    public SettlementNotFoundException(String id) {
        super("Settlement not found: " + id);
    }
}
