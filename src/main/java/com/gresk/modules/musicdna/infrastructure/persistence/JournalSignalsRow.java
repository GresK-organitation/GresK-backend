package com.gresk.modules.musicdna.infrastructure.persistence;

public interface JournalSignalsRow {
    Long getJournalCount();
    Long getWrittenCount();
    Long getTotalCustomCriteria();
    Long getLocalMatches();
}
