package com.gresk.modules.curation.domain.model;

/**
 * The two kinds of content a curated list can reference — mirrors the
 * polymorphic-reference pattern already used by agenda.LinkedEntityType.
 */
public enum ListedEntryType {
    VERIFIED_REVIEW,
    JOURNAL_ENTRY
}
