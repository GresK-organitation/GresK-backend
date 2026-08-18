package com.gresk.modules.tendencias.chronicle.infrastructure.persistence.entity;

import com.gresk.modules.tendencias.chronicle.domain.model.ChronicleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chronicles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChronicleEntity {

    @Id
    private UUID id;

    @Column(name = "feed_source_id", nullable = false)
    private UUID feedSourceId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 250)
    private String excerpt;

    @Column(nullable = false, unique = true, length = 2048)
    private String link;

    @Column(nullable = false, length = 500)
    private String guid;

    @Column(name = "source_name", nullable = false, length = 150)
    private String sourceName;

    @Column(name = "source_url", length = 2048)
    private String sourceUrl;

    @Column(name = "original_published_at")
    private Instant originalPublishedAt;

    @Column(name = "ingested_at", nullable = false)
    private Instant ingestedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChronicleStatus status;
}
