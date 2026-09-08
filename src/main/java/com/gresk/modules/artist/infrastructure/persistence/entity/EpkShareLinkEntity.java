package com.gresk.modules.artist.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "epk_share_link")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpkShareLinkEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "epk_asset_id", nullable = false)
    private UUID epkAssetId;

    @Column(name = "version_number", nullable = false)
    private int versionNumber;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(nullable = false, length = 36, unique = true)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "max_downloads")
    private Integer maxDownloads;

    @Column(name = "download_count", nullable = false)
    private int downloadCount;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "created_by_user_id", nullable = false, length = 100)
    private String createdByUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
