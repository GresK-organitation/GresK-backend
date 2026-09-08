package com.gresk.modules.finance.domain.exception;

public class InvalidInstallmentStatusTransitionException extends RuntimeException {
    public InvalidInstallmentStatusTransitionException(String message) {
        super(message);
    }
}
