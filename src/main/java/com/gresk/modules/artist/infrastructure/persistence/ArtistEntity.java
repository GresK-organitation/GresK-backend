package com.gresk.modules.artist.infrastructure.persistence;

import com.gresk.modules.artist.domain.model.ArtistStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "artists")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtistEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false)
    private UUID id;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(length = 100)
    private String origin;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false, length = 600)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ArtistStatus status;

    @Column(length = 100)
    private String fee;

    @Column(length = 50)
    private String followers;

    @Column(nullable = false, length = 255)
    private String contact;

    @Column(name = "social_spotify", length = 500)
    private String socialSpotify;

    @Column(name = "social_instagram", length = 500)
    private String socialInstagram;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "artist_genres", joinColumns = @JoinColumn(name = "artist_id"))
    @Column(name = "genre", length = 50)
    @Builder.Default
    private List<String> genres = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "artist_tags", joinColumns = @JoinColumn(name = "artist_id"))
    @Column(name = "tag", length = 100)
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Column(name = "events_played", nullable = false)
    private int eventsPlayed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public void update(String name, String origin, List<String> genres, String imageUrl, String bio,
                       ArtistStatus status, String fee, String followers, String contact,
                       String socialSpotify, String socialInstagram, List<String> tags) {
        this.name            = name;
        this.origin          = origin;
        this.genres          = genres != null ? new ArrayList<>(genres) : new ArrayList<>();
        this.imageUrl        = imageUrl;
        this.bio             = bio;
        this.status          = status;
        this.fee             = fee;
        this.followers       = followers;
        this.contact         = contact;
        this.socialSpotify   = socialSpotify;
        this.socialInstagram = socialInstagram;
        this.tags            = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }
}
