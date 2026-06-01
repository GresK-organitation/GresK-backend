package com.gresk.modules.agenda.domain.model;

import java.util.Objects;
import java.util.UUID;

public record EntityReference(LinkedEntityType type, UUID entityId) {

    public EntityReference {
        Objects.requireNonNull(type,     "EntityReference type must not be null");
        Objects.requireNonNull(entityId, "EntityReference entityId must not be null");
    }
}
