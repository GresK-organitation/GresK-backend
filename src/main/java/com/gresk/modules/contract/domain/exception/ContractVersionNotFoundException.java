package com.gresk.modules.contract.domain.exception;

public class ContractVersionNotFoundException extends RuntimeException {
    public ContractVersionNotFoundException(String id) {
        super("Contract version not found: " + id);
    }
}
