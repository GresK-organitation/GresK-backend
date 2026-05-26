-- ─────────────────────────────────────────────────────────────────────────────
-- V8: review_likes
--
-- Tracks which users have liked each review (many-to-many).
--
-- Design decisions:
--   - PK (review_id, user_id): second line of defence for uniqueness (the
--     domain's Review.addLike() is the primary guard)
--   - ON DELETE CASCADE on both FKs: likes are cleaned up automatically when
--     the parent review or user is deleted
--   - Index on review_id: @ElementCollection queries always filter by review
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE review_likes (
    review_id UUID NOT NULL,
    user_id   UUID NOT NULL,

    CONSTRAINT pk_review_likes        PRIMARY KEY (review_id, user_id),
    CONSTRAINT fk_review_likes_review FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE,
    CONSTRAINT fk_review_likes_user   FOREIGN KEY (user_id)   REFERENCES users   (id) ON DELETE CASCADE
);

CREATE INDEX idx_review_likes_review_id ON review_likes (review_id);
