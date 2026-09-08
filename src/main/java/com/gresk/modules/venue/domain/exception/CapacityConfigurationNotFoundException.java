package com.gresk.modules.venue.domain.exception;

public class CapacityConfigurationNotFoundException extends RuntimeException {
    public CapacityConfigurationNotFoundException(String code) {
        super("Capacity configuration not found: " + code);
    }
}
