package com.gresk.modules.tendencias.chronicle.infrastructure.persistence.entity;

import com.gresk.modules.tendencias.chronicle.domain.model.FeedSourceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "feed_sources")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedSourceEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "feed_url", nullable = false, unique = true, length = 2048)
    private String feedUrl;

    @Column(name = "source_url", length = 2048)
    private String sourceUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FeedSourceStatus status;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "last_fetched_at")
    private Instant lastFetchedAt;
}
