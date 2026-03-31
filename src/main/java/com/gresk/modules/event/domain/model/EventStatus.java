package com.gresk.modules.event.domain.model;

import java.util.Set;

public enum EventStatus {
    DRAFT,
    PUBLISHED,
    FINISHED,
    CANCELLED;

    private Set<EventStatus> allowedTargets;

    static {
        DRAFT.allowedTargets     = Set.of(PUBLISHED, CANCELLED);
        PUBLISHED.allowedTargets = Set.of(FINISHED, CANCELLED);
        FINISHED.allowedTargets  = Set.of();
        CANCELLED.allowedTargets = Set.of();
    }

    public boolean canTransitionTo(EventStatus target) {
        return allowedTargets.contains(target);
    }
}
