package com.gresk.modules.tendencias.chronicle.domain.model;

import com.gresk.modules.tendencias.chronicle.domain.exception.InvalidChronicleTransitionException;

import java.time.Instant;

public final class Chronicle {

    private final ChronicleId id;
    private final String title;
    private final ChronicleExcerpt excerpt;
    private final String link;
    private final String guid;
    private final SourceAttribution source;
    private final FeedSourceId feedSourceId;
    private final Instant originalPublishedAt;
    private final Instant ingestedAt;
    private ChronicleStatus status;

    private Chronicle(ChronicleId id, String title, ChronicleExcerpt excerpt, String link, String guid,
                       SourceAttribution source, FeedSourceId feedSourceId,
                       Instant originalPublishedAt, Instant ingestedAt, ChronicleStatus status) {
        this.id = id;
        this.title = title;
        this.excerpt = excerpt;
        this.link = link;
        this.guid = guid;
        this.source = source;
        this.feedSourceId = feedSourceId;
        this.originalPublishedAt = originalPublishedAt;
        this.ingestedAt = ingestedAt;
        this.status = status;
    }

    /** Publicación automática: la fuente ya fue vetada a nivel de FeedSource. */
    public static Chronicle publish(String title, ChronicleExcerpt excerpt, String link, String guid,
                                     SourceAttribution source, FeedSourceId feedSourceId,
                                     Instant originalPublishedAt) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("title must not be blank");
        if (link == null || link.isBlank())
            throw new IllegalArgumentException("link must not be blank");
        if (guid == null || guid.isBlank())
            throw new IllegalArgumentException("guid must not be blank");
        return new Chronicle(ChronicleId.generate(), title, excerpt, link, guid, source, feedSourceId,
                originalPublishedAt, Instant.now(), ChronicleStatus.PUBLISHED);
    }

    public static Chronicle reconstitute(ChronicleId id, String title, ChronicleExcerpt excerpt, String link,
                                          String guid, SourceAttribution source, FeedSourceId feedSourceId,
                                          Instant originalPublishedAt, Instant ingestedAt, ChronicleStatus status) {
        return new Chronicle(id, title, excerpt, link, guid, source, feedSourceId,
                originalPublishedAt, ingestedAt, status);
    }

    public void hide() {
        if (status != ChronicleStatus.PUBLISHED)
            throw new InvalidChronicleTransitionException("Cannot hide chronicle in status " + status);
        this.status = ChronicleStatus.HIDDEN;
    }

    public ChronicleId getId() { return id; }
    public String getTitle() { return title; }
    public ChronicleExcerpt getExcerpt() { return excerpt; }
    public String getLink() { return link; }
    public String getGuid() { return guid; }
    public SourceAttribution getSource() { return source; }
    public FeedSourceId getFeedSourceId() { return feedSourceId; }
    public Instant getOriginalPublishedAt() { return originalPublishedAt; }
    public Instant getIngestedAt() { return ingestedAt; }
    public ChronicleStatus getStatus() { return status; }
}
