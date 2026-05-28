package com.gresk.modules.artist.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "artist_metrics_snapshot",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_metrics_artist_date",
                columnNames = {"artist_id", "snapshot_date"}
        )
)
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArtistMetricsSnapshotEntity {

    @Id
    private UUID id;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "spotify_popularity")
    private Integer spotifyPopularity;

    @Column(name = "spotify_followers")
    private Integer spotifyFollowers;

    @Column(name = "last_release_date")
    private LocalDate lastReleaseDate;

    @Column(name = "total_releases")
    private Integer totalReleases;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
