package com.gresk.modules.contract.domain.exception;

public class InvalidContractStatusTransitionException extends RuntimeException {
    public InvalidContractStatusTransitionException(String message) {
        super(message);
    }
}
