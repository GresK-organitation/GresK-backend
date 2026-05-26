package com.gresk.modules.review.domain.model;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.review.domain.exception.ReviewAlreadyLikedException;
import com.gresk.modules.ticket.domain.model.TicketId;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.valueobject.ImageUrl;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Review {

    // ── Constants ─────────────────────────────────────────────────────────────
    private static final int BASE_POINTS    = 50;
    private static final int COMMENT_POINTS = 25;
    private static final int PHOTO_POINTS   = 20;

    // ── Immutable identity ────────────────────────────────────────────────────
    private final ReviewId   id;
    private final UserId     userId;
    private final EventId    eventId;
    private final TicketId   ticketId;
    private final int        pointsAwarded;
    private final Instant    createdAt;

    // ── Mutable state ─────────────────────────────────────────────────────────
    private DetailedRating detailedRating;
    private Rating         overallRating;
    private ReviewComment  comment;
    private ImageUrl       photoUrl;      // null = no photo
    private ReviewStatus   status;
    private Instant        updatedAt;
    private Set<UserId>    likedBy;

    private Review(ReviewId id, UserId userId, EventId eventId, TicketId ticketId,
                   DetailedRating detailedRating, Rating overallRating,
                   ReviewComment comment, ImageUrl photoUrl,
                   int pointsAwarded, ReviewStatus status,
                   Instant createdAt, Instant updatedAt,
                   Set<UserId> likedBy) {
        this.id             = Objects.requireNonNull(id,             "ReviewId is required");
        this.userId         = Objects.requireNonNull(userId,         "UserId is required");
        this.eventId        = Objects.requireNonNull(eventId,        "EventId is required");
        this.ticketId       = Objects.requireNonNull(ticketId,       "TicketId is required");
        this.detailedRating = Objects.requireNonNull(detailedRating, "DetailedRating is required");
        this.overallRating  = Objects.requireNonNull(overallRating,  "OverallRating is required");
        this.comment        = comment != null ? comment : ReviewComment.empty();
        this.photoUrl       = photoUrl;
        this.pointsAwarded  = pointsAwarded;
        this.status         = Objects.requireNonNull(status,         "ReviewStatus is required");
        this.createdAt      = Objects.requireNonNull(createdAt,      "CreatedAt is required");
        this.updatedAt      = Objects.requireNonNull(updatedAt,      "UpdatedAt is required");
        this.likedBy        = new HashSet<>(Objects.requireNonNull(likedBy, "likedBy is required"));
    }

    // ── Factory: new review ───────────────────────────────────────────────────

    public static Review create(UserId userId, EventId eventId, TicketId ticketId,
                                DetailedRating detailedRating,
                                ReviewComment comment, ImageUrl photoUrl) {
        int points = BASE_POINTS;
        if (comment != null && !comment.isEmpty())   points += COMMENT_POINTS;
        if (photoUrl != null && !photoUrl.isEmpty()) points += PHOTO_POINTS;

        Instant now = Instant.now();
        return new Review(
            ReviewId.generate(), userId, eventId, ticketId,
            detailedRating, detailedRating.overall(),
            comment, photoUrl, points, ReviewStatus.PUBLISHED,
            now, now,
            new HashSet<>()
        );
    }

    // ── Factory: reconstitute from persistence ────────────────────────────────

    public static Review reconstitute(ReviewId id, UserId userId, EventId eventId,
                                      TicketId ticketId, DetailedRating detailedRating,
                                      Rating overallRating, ReviewComment comment,
                                      ImageUrl photoUrl, int pointsAwarded,
                                      ReviewStatus status, Instant createdAt,
                                      Instant updatedAt, Set<UserId> likedBy) {
        return new Review(id, userId, eventId, ticketId,
            detailedRating, overallRating, comment, photoUrl,
            pointsAwarded, status, createdAt, updatedAt, likedBy);
    }

    // ── Behaviours ────────────────────────────────────────────────────────────

    /**
     * Edit the review content. Points are NOT recalculated —
     * the reward was already granted on initial submission.
     */
    public void update(DetailedRating newRating, ReviewComment newComment,
                       ImageUrl newPhotoUrl) {
        this.detailedRating = Objects.requireNonNull(newRating);
        this.overallRating  = newRating.overall();
        this.comment        = newComment != null ? newComment : ReviewComment.empty();
        this.photoUrl       = newPhotoUrl;
        this.updatedAt      = Instant.now();
    }

    public void hide() {
        this.status    = ReviewStatus.HIDDEN;
        this.updatedAt = Instant.now();
    }

    public void addLike(UserId userId) {
        if (likedBy.contains(userId)) {
            throw new ReviewAlreadyLikedException(
                    "User " + userId.value() + " has already liked this review");
        }
        likedBy.add(userId);
    }

    public void removeLike(UserId userId) {
        likedBy.remove(userId); // idempotente: no lanza si el like no existía
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public ReviewId       getId()              { return id; }
    public UserId         getUserId()          { return userId; }
    public EventId        getEventId()         { return eventId; }
    public TicketId       getTicketId()        { return ticketId; }
    public DetailedRating getDetailedRating()  { return detailedRating; }
    public Rating         getOverallRating()   { return overallRating; }
    public ReviewComment  getComment()         { return comment; }
    public ImageUrl       getPhotoUrl()        { return photoUrl; }
    public int            getPointsAwarded()   { return pointsAwarded; }
    public ReviewStatus   getStatus()          { return status; }
    public Instant        getCreatedAt()       { return createdAt; }
    public Instant        getUpdatedAt()       { return updatedAt; }
    public Set<UserId>    getLikedBy()         { return Collections.unmodifiableSet(likedBy); }
    public int            getLikeCount()       { return likedBy.size(); }
}
