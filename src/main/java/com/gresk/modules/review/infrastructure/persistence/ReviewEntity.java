package com.gresk.modules.review.infrastructure.persistence;

import com.gresk.modules.review.domain.model.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "ticket_id", nullable = false, unique = true)
    private UUID ticketId;

    @Column(name = "artist_rating", nullable = false)
    private int artistRating;

    @Column(name = "sound_rating", nullable = false)
    private int soundRating;

    @Column(name = "ambience_rating", nullable = false)
    private int ambienceRating;

    @Column(name = "venue_rating", nullable = false)
    private int venueRating;

    @Column(name = "setlist_rating", nullable = false)
    private int setlistRating;

    @Column(name = "overall_rating", nullable = false)
    private int overallRating;

    @Column(name = "comment", length = 300)
    private String comment;

    @Column(name = "photo_url", length = 512)
    private String photoUrl;

    @Column(name = "points_awarded", nullable = false)
    private int pointsAwarded;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReviewStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "review_likes",
            joinColumns = @JoinColumn(name = "review_id")
    )
    @Column(name = "user_id", nullable = false)
    private Set<UUID> likedBy = new HashSet<>();
}
