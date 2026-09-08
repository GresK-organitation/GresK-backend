package com.gresk.modules.venue.domain.exception;

public class DuplicateCapacityConfigurationException extends RuntimeException {
    public DuplicateCapacityConfigurationException(String code) {
        super("A capacity configuration with code '" + code + "' already exists for this venue");
    }
}
