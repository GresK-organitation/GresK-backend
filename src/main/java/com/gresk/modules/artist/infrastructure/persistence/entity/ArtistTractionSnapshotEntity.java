package com.gresk.modules.artist.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "artist_traction_snapshot",
        uniqueConstraints = @UniqueConstraint(name = "uq_traction_artist_date", columnNames = {"artist_id", "snapshot_date"})
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistTractionSnapshotEntity {

    @Id
    private UUID id;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "bandsintown_followers")
    private Integer bandsintownFollowers;

    @Column(name = "bandsintown_upcoming_shows")
    private Integer bandsintownUpcomingShows;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "artist_traction_city_audience", joinColumns = @JoinColumn(name = "snapshot_id"))
    @Builder.Default
    private List<CityAudienceEmbeddable> cityAudience = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
