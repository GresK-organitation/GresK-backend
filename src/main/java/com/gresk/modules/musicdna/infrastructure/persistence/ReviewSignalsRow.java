package com.gresk.modules.musicdna.infrastructure.persistence;

public interface ReviewSignalsRow {
    Long getReviewCount();
    Long getWrittenCount();
    Long getTotalLikes();
    Long getLocalMatches();
}
