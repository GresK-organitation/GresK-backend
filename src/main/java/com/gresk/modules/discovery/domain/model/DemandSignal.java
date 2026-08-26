package com.gresk.modules.discovery.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.user.domain.model.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * Señal de demanda: "quiero que vengan a mi ciudad". Un usuario solo puede
 * tener una señal activa por artista — se modela como toggle (crear/borrar),
 * nunca se acumulan duplicados para el mismo (artistId, userId).
 */
public final class DemandSignal {

    private final DemandSignalId id;
    private final ArtistId artistId;
    private final UserId userId;
    private final String city;
    private final Instant createdAt;

    private DemandSignal(DemandSignalId id, ArtistId artistId, UserId userId, String city, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "DemandSignalId is required");
        this.artistId = Objects.requireNonNull(artistId, "ArtistId is required");
        this.userId = Objects.requireNonNull(userId, "UserId is required");
        this.city = Objects.requireNonNull(city, "city is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
    }

    public static DemandSignal create(ArtistId artistId, UserId userId, String city) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("city is required to raise a demand signal");
        }
        return new DemandSignal(DemandSignalId.generate(), artistId, userId, city, Instant.now());
    }

    public static DemandSignal reconstitute(DemandSignalId id, ArtistId artistId, UserId userId, String city,
                                             Instant createdAt) {
        return new DemandSignal(id, artistId, userId, city, createdAt);
    }

    public DemandSignalId getId() { return id; }
    public ArtistId getArtistId() { return artistId; }
    public UserId getUserId() { return userId; }
    public String getCity() { return city; }
    public Instant getCreatedAt() { return createdAt; }
}
