package com.gresk.modules.contract.domain.model;

import java.util.UUID;

public record ClauseTemplateId(UUID value) {

    public ClauseTemplateId {
        if (value == null) throw new IllegalArgumentException("ClauseTemplateId cannot be null");
    }

    public static ClauseTemplateId generate() {
        return new ClauseTemplateId(UUID.randomUUID());
    }

    public static ClauseTemplateId of(String value) {
        try {
            return new ClauseTemplateId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ClauseTemplateId format: " + value, e);
        }
    }

    public static ClauseTemplateId of(UUID value) {
        return new ClauseTemplateId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
