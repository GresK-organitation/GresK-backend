package com.gresk.modules.curation.domain.model;

import com.gresk.modules.curation.domain.exception.DuplicateListItemException;
import com.gresk.modules.curation.domain.exception.InvalidCuratedListException;
import com.gresk.modules.curation.domain.exception.ListItemNotFoundException;
import com.gresk.modules.user.domain.model.UserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A themed collection of the owner's reviews and/or journal entries
 * ("Mejores conciertos de jazz 2025"), shareable and followable.
 * References items polymorphically by (type, id) — it never loads or
 * mutates the referenced Review/JournalEntry aggregates themselves.
 */
public final class CuratedList {

    private static final int MAX_TITLE_LENGTH       = 120;
    private static final int MAX_DESCRIPTION_LENGTH  = 500;
    private static final int MAX_ITEMS               = 200;

    private final CuratedListId id;
    private final UserId        ownerId;
    private final Instant       createdAt;

    private String              title;
    private String              description;
    private ListVisibility      visibility;
    private List<CuratedListItem> items;
    private Instant              updatedAt;

    private CuratedList(CuratedListId id, UserId ownerId, Instant createdAt,
                         String title, String description, ListVisibility visibility,
                         List<CuratedListItem> items, Instant updatedAt) {
        this.id        = Objects.requireNonNull(id, "CuratedListId is required");
        this.ownerId   = Objects.requireNonNull(ownerId, "UserId is required");
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt is required");
        this.visibility = Objects.requireNonNull(visibility, "ListVisibility is required");
        this.updatedAt  = Objects.requireNonNull(updatedAt, "UpdatedAt is required");
        this.title       = validateTitle(title);
        this.description = validateDescription(description);
        this.items        = new ArrayList<>(Objects.requireNonNull(items, "items is required"));
    }

    public static CuratedList create(UserId ownerId, String title, String description, ListVisibility visibility) {
        Instant now = Instant.now();
        return new CuratedList(CuratedListId.generate(), ownerId, now, title, description, visibility, new ArrayList<>(), now);
    }

    public static CuratedList reconstitute(CuratedListId id, UserId ownerId, Instant createdAt,
                                            String title, String description, ListVisibility visibility,
                                            List<CuratedListItem> items, Instant updatedAt) {
        return new CuratedList(id, ownerId, createdAt, title, description, visibility, items, updatedAt);
    }

    // ── Behaviours ────────────────────────────────────────────────────────────

    public void rename(String newTitle, String newDescription) {
        this.title       = validateTitle(newTitle);
        this.description = validateDescription(newDescription);
        this.updatedAt    = Instant.now();
    }

    public void changeVisibility(ListVisibility newVisibility) {
        this.visibility = Objects.requireNonNull(newVisibility, "ListVisibility is required");
        this.updatedAt   = Instant.now();
    }

    public CuratedListItem addItem(ListedEntryType entryType, UUID entryId) {
        boolean alreadyPresent = items.stream()
                .anyMatch(i -> i.entryType() == entryType && i.entryId().equals(entryId));
        if (alreadyPresent) {
            throw new DuplicateListItemException("This item is already in the list");
        }
        if (items.size() >= MAX_ITEMS) {
            throw new InvalidCuratedListException("A list cannot contain more than " + MAX_ITEMS + " items");
        }

        CuratedListItem item = CuratedListItem.create(entryType, entryId, items.size());
        items.add(item);
        updatedAt = Instant.now();
        return item;
    }

    public void removeItem(CuratedListItemId itemId) {
        boolean removed = items.removeIf(i -> i.id().equals(itemId));
        if (!removed) {
            throw new ListItemNotFoundException("List item not found: " + itemId);
        }
        compactPositions();
        updatedAt = Instant.now();
    }

    public void reorder(List<CuratedListItemId> orderedItemIds) {
        if (orderedItemIds.size() != items.size()
                || !itemIds().containsAll(orderedItemIds)
                || !orderedItemIds.containsAll(itemIds())) {
            throw new InvalidCuratedListException("Reorder must include every current item exactly once");
        }

        List<CuratedListItem> reordered = new ArrayList<>(items.size());
        for (int position = 0; position < orderedItemIds.size(); position++) {
            CuratedListItemId itemId = orderedItemIds.get(position);
            CuratedListItem current = items.stream()
                    .filter(i -> i.id().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new ListItemNotFoundException("List item not found: " + itemId));
            reordered.add(current.withPosition(position));
        }
        this.items    = reordered;
        this.updatedAt = Instant.now();
    }

    private List<CuratedListItemId> itemIds() {
        return items.stream().map(CuratedListItem::id).toList();
    }

    private void compactPositions() {
        List<CuratedListItem> compacted = new ArrayList<>(items.size());
        for (int position = 0; position < items.size(); position++) {
            compacted.add(items.get(position).withPosition(position));
        }
        this.items = compacted;
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private static String validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidCuratedListException("Title must not be blank");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new InvalidCuratedListException("Title must not exceed " + MAX_TITLE_LENGTH + " characters");
        }
        return title.strip();
    }

    private static String validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new InvalidCuratedListException("Description must not exceed " + MAX_DESCRIPTION_LENGTH + " characters");
        }
        return (description == null || description.isBlank()) ? null : description.strip();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public CuratedListId  getId()          { return id; }
    public UserId          getOwnerId()     { return ownerId; }
    public Instant         getCreatedAt()   { return createdAt; }
    public String          getTitle()       { return title; }
    public String          getDescription() { return description; }
    public ListVisibility  getVisibility()  { return visibility; }
    public List<CuratedListItem> getItems() { return Collections.unmodifiableList(items); }
    public Instant          getUpdatedAt()  { return updatedAt; }
}
