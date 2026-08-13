package com.gresk.modules.curation.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A single entry in a curated list, referencing content owned by another
 * module (review.Review or journal.JournalEntry) purely by id — no FK,
 * since a single column can't target two different tables. Existence is
 * validated at the application layer via read-only lookup ports.
 */
public record CuratedListItem(
        CuratedListItemId id,
        ListedEntryType   entryType,
        UUID              entryId,
        int               position,
        Instant           addedAt
) {
    public CuratedListItem {
        Objects.requireNonNull(id, "CuratedListItemId is required");
        Objects.requireNonNull(entryType, "ListedEntryType is required");
        Objects.requireNonNull(entryId, "entryId is required");
        Objects.requireNonNull(addedAt, "addedAt is required");
        if (position < 0) {
            throw new IllegalArgumentException("position must not be negative");
        }
    }

    public static CuratedListItem create(ListedEntryType entryType, UUID entryId, int position) {
        return new CuratedListItem(CuratedListItemId.generate(), entryType, entryId, position, Instant.now());
    }

    public CuratedListItem withPosition(int newPosition) {
        return new CuratedListItem(id, entryType, entryId, newPosition, addedAt);
    }
}
