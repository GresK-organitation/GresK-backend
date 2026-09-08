package com.gresk.modules.contract.domain.exception;

public class ContractTemplateNotFoundException extends RuntimeException {
    public ContractTemplateNotFoundException(String id) {
        super("Contract template not found: " + id);
    }
}
