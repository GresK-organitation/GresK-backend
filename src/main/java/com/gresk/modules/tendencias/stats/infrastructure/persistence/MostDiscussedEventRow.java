package com.gresk.modules.tendencias.stats.infrastructure.persistence;

import java.util.UUID;

public interface MostDiscussedEventRow {
    UUID getEventId();
    String getEventTitle();
    Long getReviewCount();
}
