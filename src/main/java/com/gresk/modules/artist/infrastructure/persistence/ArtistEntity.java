package com.gresk.modules.artist.infrastructure.persistence;

import com.gresk.modules.artist.domain.model.valueobject.ArtistStatus;
import com.gresk.shared.domain.MusicGenre;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "artists")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtistEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String origin;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "artist_genres", joinColumns = @JoinColumn(name = "artist_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "genre", length = 50)
    @Builder.Default
    private Set<MusicGenre> genres = new HashSet<>();

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @Column(length = 600)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ArtistStatus status;

    @Column(length = 100)
    private String fee;

    @Column(length = 50)
    private String followers;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "artist_tags", joinColumns = @JoinColumn(name = "artist_id"))
    @Column(name = "tag", length = 100)
    @Builder.Default
    private Set<String> tags = new HashSet<>();

    @Column(nullable = false, length = 255)
    private String contact;

    @Column(name = "instagram_url", length = 500)
    private String instagramUrl;

    @Column(name = "spotify_url", length = 500)
    private String spotifyUrl;

    @Column(name = "events_played", nullable = false)
    private int eventsPlayed;

    @Column(name = "avg_rating", nullable = false)
    private double avgRating;

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

    public void updateProfile(String name, String origin, Set<MusicGenre> genres,
                               String imageUrl, String bio) {
        this.name     = name;
        this.origin   = origin;
        this.genres   = new HashSet<>(genres);
        this.imageUrl = imageUrl;
        this.bio      = bio;
    }

    public void updateProfessionalInfo(ArtistStatus status, String fee,
                                       String followers, Set<String> tags) {
        this.status    = status;
        this.fee       = fee;
        this.followers = followers;
        this.tags      = new HashSet<>(tags);
    }

    public void updateContact(String contact, String instagramUrl, String spotifyUrl) {
        this.contact      = contact;
        this.instagramUrl = instagramUrl;
        this.spotifyUrl   = spotifyUrl;
    }

    public void updateStats(int eventsPlayed, double avgRating) {
        this.eventsPlayed = eventsPlayed;
        this.avgRating    = avgRating;
    }
}
