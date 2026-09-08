package com.gresk.modules.contract.domain.exception;

public class ClauseTemplateNotOwnedException extends RuntimeException {
    public ClauseTemplateNotOwnedException() {
        super("You do not have permission to modify this clause template");
    }
}
