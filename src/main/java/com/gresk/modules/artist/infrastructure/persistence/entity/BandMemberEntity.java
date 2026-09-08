package com.gresk.modules.artist.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "band_member")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BandMemberEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "role_in_band", nullable = false, length = 100)
    private String roleInBand;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "band_member_document", joinColumns = @JoinColumn(name = "band_member_id"))
    @Builder.Default
    private List<IdentityDocumentEmbeddable> documents = new ArrayList<>();

    @Column(nullable = false)
    private boolean active;

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

    public void updateProfile(String name, String roleInBand, boolean active) {
        this.name       = name;
        this.roleInBand = roleInBand;
        this.active     = active;
    }

    public void replaceDocuments(List<IdentityDocumentEmbeddable> documents) {
        this.documents = new ArrayList<>(documents);
    }
}
