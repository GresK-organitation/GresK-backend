package com.gresk.modules.artist.infrastructure.persistence.entity;

import com.gresk.modules.artist.domain.model.valueobject.EpkAssetType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "epk_asset")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EpkAssetEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EpkAssetType type;

    @Column(nullable = false, length = 150)
    private String label;

    @Column(nullable = false)
    private boolean archived;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "epk_asset_version", joinColumns = @JoinColumn(name = "epk_asset_id"))
    @OrderBy("versionNumber ASC")
    @Builder.Default
    private List<EpkAssetVersionEmbeddable> versions = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void updateLabel(String label) {
        this.label = label;
    }

    public void updateArchived(boolean archived) {
        this.archived = archived;
    }

    public void replaceVersions(List<EpkAssetVersionEmbeddable> versions) {
        this.versions = new ArrayList<>(versions);
    }
}
