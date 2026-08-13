package com.gresk.modules.journal.infrastructure.persistence.entity;

import com.gresk.modules.journal.domain.model.JournalMediaType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "journal_entry_media")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JournalEntryMediaEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id", nullable = false)
    private JournalEntryEntity entry;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", length = 10, nullable = false)
    private JournalMediaType mediaType;

    @Column(name = "asset_id", length = 255, nullable = false)
    private String assetId;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
