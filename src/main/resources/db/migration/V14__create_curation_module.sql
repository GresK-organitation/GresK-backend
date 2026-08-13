-- ─────────────────────────────────────────────────────────────────────────────
-- V14: curated_lists + curated_list_items + list_followers
--
-- Curated list aggregate ("listas curadas"):
--   Themed collections of the owner's reviews and/or journal entries
--   ("Mejores conciertos de jazz 2025"), shareable and followable.
--   id, owner_id (FK→users), title, description, visibility (PRIVATE|PUBLIC)
--
-- List items are polymorphic references (no FK — a single column can't
-- target two different tables): entry_type (VERIFIED_REVIEW|JOURNAL_ENTRY)
-- + entry_id, ordered by position. Existence of the referenced row is
-- validated at the application layer via read-only lookup ports, mirroring
-- the pattern already used by agenda.EntityReference/LinkedEntityType.
--
-- Follow relationship: list_id + user_id (composite PK), ON DELETE CASCADE
-- on both sides — same pattern as review_likes.
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE curated_lists (
    id            UUID         NOT NULL,
    owner_id      UUID         NOT NULL,
    title         VARCHAR(120) NOT NULL,
    description   VARCHAR(500),
    visibility    VARCHAR(10)  NOT NULL DEFAULT 'PRIVATE',
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_curated_lists       PRIMARY KEY (id),
    CONSTRAINT fk_curated_lists_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT chk_curated_lists_vis  CHECK (visibility IN ('PUBLIC', 'PRIVATE'))
);

CREATE INDEX idx_curated_lists_owner_id   ON curated_lists (owner_id);
CREATE INDEX idx_curated_lists_visibility ON curated_lists (visibility);


-- ── Ordered polymorphic list items ──────────────────────────────────────────

CREATE TABLE curated_list_items (
    id           UUID         NOT NULL,
    list_id      UUID         NOT NULL,
    entry_type   VARCHAR(20)  NOT NULL,
    entry_id     UUID         NOT NULL,
    position     INTEGER      NOT NULL,
    added_at     TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_curated_list_items       PRIMARY KEY (id),
    CONSTRAINT fk_curated_list_items_list  FOREIGN KEY (list_id) REFERENCES curated_lists (id) ON DELETE CASCADE,
    CONSTRAINT uq_curated_list_items_pos   UNIQUE (list_id, position),
    CONSTRAINT uq_curated_list_items_entry UNIQUE (list_id, entry_type, entry_id),
    CONSTRAINT chk_curated_list_items_type CHECK (entry_type IN ('VERIFIED_REVIEW', 'JOURNAL_ENTRY'))
);

CREATE INDEX idx_curated_list_items_list_id ON curated_list_items (list_id);


-- ── Follow relationship ──────────────────────────────────────────────────────

CREATE TABLE list_followers (
    list_id      UUID        NOT NULL,
    user_id      UUID        NOT NULL,
    followed_at  TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_list_followers      PRIMARY KEY (list_id, user_id),
    CONSTRAINT fk_list_followers_list FOREIGN KEY (list_id) REFERENCES curated_lists (id) ON DELETE CASCADE,
    CONSTRAINT fk_list_followers_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_list_followers_list_id ON list_followers (list_id);
CREATE INDEX idx_list_followers_user_id ON list_followers (user_id);
