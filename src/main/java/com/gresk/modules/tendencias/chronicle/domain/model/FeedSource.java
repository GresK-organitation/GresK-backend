package com.gresk.modules.tendencias.chronicle.domain.model;

import com.gresk.modules.tendencias.chronicle.domain.exception.InvalidFeedSourceTransitionException;

import java.time.Instant;
import java.util.UUID;

/**
 * Catálogo de medios (feeds RSS). La moderación ocurre a este nivel, no por
 * artículo: un ADMIN aprueba el medio una vez y, a partir de ahí, todo lo que
 * llegue de un FeedSource APPROVED se publica automáticamente.
 */
public final class FeedSource {

    private final FeedSourceId id;
    private final String name;
    private final String feedUrl;
    private final String sourceUrl;
    private final Instant requestedAt;
    private FeedSourceStatus status;
    private UUID reviewedBy;
    private Instant reviewedAt;
    private Instant lastFetchedAt;

    private FeedSource(FeedSourceId id, String name, String feedUrl, String sourceUrl, Instant requestedAt,
                        FeedSourceStatus status, UUID reviewedBy, Instant reviewedAt, Instant lastFetchedAt) {
        this.id = id;
        this.name = name;
        this.feedUrl = feedUrl;
        this.sourceUrl = sourceUrl;
        this.requestedAt = requestedAt;
        this.status = status;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
        this.lastFetchedAt = lastFetchedAt;
    }

    public static FeedSource register(String name, String feedUrl, String sourceUrl) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("name must not be blank");
        if (feedUrl == null || feedUrl.isBlank())
            throw new IllegalArgumentException("feedUrl must not be blank");
        return new FeedSource(FeedSourceId.generate(), name, feedUrl, sourceUrl, Instant.now(),
                FeedSourceStatus.PENDING_APPROVAL, null, null, null);
    }

    public static FeedSource reconstitute(FeedSourceId id, String name, String feedUrl, String sourceUrl,
                                           Instant requestedAt, FeedSourceStatus status, UUID reviewedBy,
                                           Instant reviewedAt, Instant lastFetchedAt) {
        return new FeedSource(id, name, feedUrl, sourceUrl, requestedAt, status, reviewedBy, reviewedAt, lastFetchedAt);
    }

    public void approve(UUID adminId) {
        if (status == FeedSourceStatus.APPROVED) return;
        this.status = FeedSourceStatus.APPROVED;
        this.reviewedBy = adminId;
        this.reviewedAt = Instant.now();
    }

    public void reject(UUID adminId) {
        if (status == FeedSourceStatus.REJECTED)
            throw new InvalidFeedSourceTransitionException("Feed source already REJECTED");
        this.status = FeedSourceStatus.REJECTED;
        this.reviewedBy = adminId;
        this.reviewedAt = Instant.now();
    }

    public void markFetched(Instant when) {
        this.lastFetchedAt = when;
    }

    public SourceAttribution attribution() {
        return new SourceAttribution(name, sourceUrl);
    }

    public FeedSourceId getId() { return id; }
    public String getName() { return name; }
    public String getFeedUrl() { return feedUrl; }
    public String getSourceUrl() { return sourceUrl; }
    public Instant getRequestedAt() { return requestedAt; }
    public FeedSourceStatus getStatus() { return status; }
    public UUID getReviewedBy() { return reviewedBy; }
    public Instant getReviewedAt() { return reviewedAt; }
    public Instant getLastFetchedAt() { return lastFetchedAt; }
}
