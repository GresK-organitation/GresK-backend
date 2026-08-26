package com.gresk.modules.discovery.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "artist_discovery_profile")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDiscoveryProfileEntity {

    @Id
    private UUID id;

    @Column(name = "artist_id", nullable = false, unique = true)
    private UUID artistId;

    @Column(name = "size_tier", nullable = false, length = 20)
    private String sizeTier;

    @Column(name = "spotify_popularity")
    private Integer spotifyPopularity;

    @Column(name = "musicbrainz_id", length = 50)
    private String musicBrainzId;

    @Column(name = "mb_country", length = 100)
    private String mbCountry;

    @Column(name = "mb_city", length = 100)
    private String mbCity;

    @Column(name = "mb_begin_year")
    private Integer mbBeginYear;

    @Column(name = "gresk_review_count", nullable = false)
    private int greskReviewCount;

    @Column(name = "gresk_demand_count", nullable = false)
    private int greskDemandCount;

    @Column(name = "gresk_verified_attendees", nullable = false)
    private int greskVerifiedAttendees;

    @Column(name = "known_by_count", nullable = false)
    private int knownByCount;

    @Column(name = "gresk_score", nullable = false)
    private BigDecimal greskScore;

    @Column(name = "has_upcoming_events", nullable = false)
    private boolean hasUpcomingEvents;

    @Column(name = "next_event_date")
    private LocalDate nextEventDate;

    @Column(name = "next_event_city", length = 100)
    private String nextEventCity;

    @Column(name = "first_discovered_at", nullable = false)
    private Instant firstDiscoveredAt;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt;
}
