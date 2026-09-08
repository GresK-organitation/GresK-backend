package com.gresk.modules.finance.domain.exception;

public class InvalidSettlementStatusTransitionException extends RuntimeException {
    public InvalidSettlementStatusTransitionException(String message) {
        super(message);
    }
}
