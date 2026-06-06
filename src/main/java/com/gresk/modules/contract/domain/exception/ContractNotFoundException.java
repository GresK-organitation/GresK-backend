package com.gresk.modules.contract.domain.exception;

public class ContractNotFoundException extends RuntimeException {
    public ContractNotFoundException(String id) {
        super("Contract not found: " + id);
    }
}
