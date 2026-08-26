package com.gresk.modules.discovery.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.port.out.DiscoveryProfileSignals;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Perfil de descubrimiento de un artista: agregado materializado 1:1 con
 * {@link ArtistId}, propiedad del módulo `discovery` (no de `artist`).
 * Se recalcula por completo cada vez (nunca se muta un campo aislado),
 * mismo criterio que {@code UserMusicDna}.
 */
public final class ArtistDiscoveryProfile {

    private final ArtistDiscoveryProfileId id;
    private final ArtistId artistId;
    private final SizeTier sizeTier;
    private final Integer spotifyPopularity;
    private final String musicBrainzId;
    private final String mbCountry;
    private final String mbCity;
    private final Integer mbBeginYear;
    private final int greskReviewCount;
    private final int greskDemandCount;
    private final int greskVerifiedAttendees;
    private final int knownByCount;
    private final BigDecimal greskScore;
    private final boolean hasUpcomingEvents;
    private final LocalDate nextEventDate;
    private final String nextEventCity;
    private final Instant firstDiscoveredAt;
    private final Instant calculatedAt;

    private ArtistDiscoveryProfile(ArtistDiscoveryProfileId id, ArtistId artistId, SizeTier sizeTier,
                                    Integer spotifyPopularity, String musicBrainzId, String mbCountry,
                                    String mbCity, Integer mbBeginYear, int greskReviewCount,
                                    int greskDemandCount, int greskVerifiedAttendees, int knownByCount,
                                    BigDecimal greskScore, boolean hasUpcomingEvents, LocalDate nextEventDate,
                                    String nextEventCity, Instant firstDiscoveredAt, Instant calculatedAt) {
        this.id = Objects.requireNonNull(id, "ArtistDiscoveryProfileId is required");
        this.artistId = Objects.requireNonNull(artistId, "ArtistId is required");
        this.sizeTier = Objects.requireNonNull(sizeTier, "SizeTier is required");
        this.spotifyPopularity = spotifyPopularity;
        this.musicBrainzId = musicBrainzId;
        this.mbCountry = mbCountry;
        this.mbCity = mbCity;
        this.mbBeginYear = mbBeginYear;
        this.greskReviewCount = greskReviewCount;
        this.greskDemandCount = greskDemandCount;
        this.greskVerifiedAttendees = greskVerifiedAttendees;
        this.knownByCount = knownByCount;
        this.greskScore = Objects.requireNonNull(greskScore, "greskScore is required");
        this.hasUpcomingEvents = hasUpcomingEvents;
        this.nextEventDate = nextEventDate;
        this.nextEventCity = nextEventCity;
        this.firstDiscoveredAt = Objects.requireNonNull(firstDiscoveredAt, "firstDiscoveredAt is required");
        this.calculatedAt = Objects.requireNonNull(calculatedAt, "calculatedAt is required");
    }

    /** Recalcula el perfil completo a partir de las señales agregadas del artista. */
    public static ArtistDiscoveryProfile calculate(ArtistId artistId, DiscoveryProfileSignals signals) {
        Objects.requireNonNull(signals, "DiscoveryProfileSignals is required");

        SizeTier sizeTier = SizeTier.fromPopularity(signals.spotifyPopularity());
        BigDecimal score = DiscoveryScoreFormulas.greskScore(signals);
        int knownBy = (int) Math.max(signals.knownByCount(), 0);

        return new ArtistDiscoveryProfile(
                ArtistDiscoveryProfileId.generate(), artistId, sizeTier,
                signals.spotifyPopularity(), signals.musicBrainzId(), signals.mbCountry(),
                signals.mbCity(), signals.mbBeginYear(),
                (int) signals.reviewCount(), (int) signals.demandCount(),
                (int) signals.verifiedAttendeesCount(), knownBy,
                score, signals.hasUpcomingEvents(), signals.nextEventDate(), signals.nextEventCity(),
                signals.artistCreatedAt(), Instant.now()
        );
    }

    public static ArtistDiscoveryProfile reconstitute(ArtistDiscoveryProfileId id, ArtistId artistId,
            SizeTier sizeTier, Integer spotifyPopularity, String musicBrainzId, String mbCountry, String mbCity,
            Integer mbBeginYear, int greskReviewCount, int greskDemandCount, int greskVerifiedAttendees,
            int knownByCount, BigDecimal greskScore, boolean hasUpcomingEvents, LocalDate nextEventDate,
            String nextEventCity, Instant firstDiscoveredAt, Instant calculatedAt) {
        return new ArtistDiscoveryProfile(id, artistId, sizeTier, spotifyPopularity, musicBrainzId, mbCountry,
                mbCity, mbBeginYear, greskReviewCount, greskDemandCount, greskVerifiedAttendees, knownByCount,
                greskScore, hasUpcomingEvents, nextEventDate, nextEventCity, firstDiscoveredAt, calculatedAt);
    }

    /** Refresco ligero diario: solo actividad en vivo, sin recalcular el score completo. */
    public ArtistDiscoveryProfile withUpcomingEventFlags(boolean hasUpcomingEvents, LocalDate nextEventDate,
                                                           String nextEventCity) {
        return new ArtistDiscoveryProfile(id, artistId, sizeTier, spotifyPopularity, musicBrainzId, mbCountry,
                mbCity, mbBeginYear, greskReviewCount, greskDemandCount, greskVerifiedAttendees, knownByCount,
                greskScore, hasUpcomingEvents, nextEventDate, nextEventCity, firstDiscoveredAt, Instant.now());
    }

    /** Enriquecimiento mensual vía MusicBrainz: solo toca los campos geográficos/estructurales. */
    public ArtistDiscoveryProfile withMusicBrainzInfo(String musicBrainzId, String mbCountry, String mbCity,
                                                        Integer mbBeginYear) {
        return new ArtistDiscoveryProfile(id, artistId, sizeTier, spotifyPopularity, musicBrainzId, mbCountry,
                mbCity, mbBeginYear, greskReviewCount, greskDemandCount, greskVerifiedAttendees, knownByCount,
                greskScore, hasUpcomingEvents, nextEventDate, nextEventCity, firstDiscoveredAt, Instant.now());
    }

    public ArtistDiscoveryProfileId getId() { return id; }
    public ArtistId getArtistId() { return artistId; }
    public SizeTier getSizeTier() { return sizeTier; }
    public Integer getSpotifyPopularity() { return spotifyPopularity; }
    public String getMusicBrainzId() { return musicBrainzId; }
    public String getMbCountry() { return mbCountry; }
    public String getMbCity() { return mbCity; }
    public Integer getMbBeginYear() { return mbBeginYear; }
    public int getGreskReviewCount() { return greskReviewCount; }
    public int getGreskDemandCount() { return greskDemandCount; }
    public int getGreskVerifiedAttendees() { return greskVerifiedAttendees; }
    public int getKnownByCount() { return knownByCount; }
    public BigDecimal getGreskScore() { return greskScore; }
    public boolean isHasUpcomingEvents() { return hasUpcomingEvents; }
    public LocalDate getNextEventDate() { return nextEventDate; }
    public String getNextEventCity() { return nextEventCity; }
    public Instant getFirstDiscoveredAt() { return firstDiscoveredAt; }
    public Instant getCalculatedAt() { return calculatedAt; }
}
