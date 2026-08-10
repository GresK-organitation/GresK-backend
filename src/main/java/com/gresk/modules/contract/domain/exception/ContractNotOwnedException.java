package com.gresk.modules.contract.domain.exception;

public class ContractNotOwnedException extends RuntimeException {
    public ContractNotOwnedException() {
        super("You do not have permission to access this contract");
    }
}
