package com.gresk.modules.finance.domain.exception;

public class CostLineNotFoundException extends RuntimeException {
    public CostLineNotFoundException(String id) {
        super("Cost line not found: " + id);
    }
}
