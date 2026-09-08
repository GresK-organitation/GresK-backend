package com.gresk.modules.contract.domain.exception;

public class ContractTemplateNotOwnedException extends RuntimeException {
    public ContractTemplateNotOwnedException() {
        super("You do not have permission to modify this contract template");
    }
}
