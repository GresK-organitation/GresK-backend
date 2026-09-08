package com.gresk.modules.contract.domain.exception;

public class ClauseTemplateNotFoundException extends RuntimeException {
    public ClauseTemplateNotFoundException(String id) {
        super("Clause template not found: " + id);
    }
}
