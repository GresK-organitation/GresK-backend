package com.gresk.modules.event.domain.model;

import java.util.Set;

public enum EventStatus {
    DRAFT,
    PUBLISHED,
    SOLD_OUT,
    FINISHED,
    CANCELLED;

    private Set<EventStatus> allowedTargets;

    static {
        DRAFT.allowedTargets     = Set.of(PUBLISHED, CANCELLED);
        PUBLISHED.allowedTargets = Set.of(SOLD_OUT, FINISHED, CANCELLED);
        SOLD_OUT.allowedTargets  = Set.of(PUBLISHED, FINISHED, CANCELLED);
        FINISHED.allowedTargets  = Set.of();
        CANCELLED.allowedTargets = Set.of();
    }

    public boolean canTransitionTo(EventStatus target) {
        return allowedTargets.contains(target);
    }
}
