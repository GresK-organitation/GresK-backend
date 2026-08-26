package com.gresk.modules.discovery.domain.model;

import java.util.UUID;

public record ArtistDiscoveryProfileId(UUID value) {

    public ArtistDiscoveryProfileId {
        if (value == null) throw new IllegalArgumentException("ArtistDiscoveryProfileId must not be null");
    }

    public static ArtistDiscoveryProfileId generate() { return new ArtistDiscoveryProfileId(UUID.randomUUID()); }

    public static ArtistDiscoveryProfileId of(UUID value) { return new ArtistDiscoveryProfileId(value); }

    public static ArtistDiscoveryProfileId of(String value) {
        try { return new ArtistDiscoveryProfileId(UUID.fromString(value)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ArtistDiscoveryProfileId: " + value);
        }
    }

    @Override
    public String toString() { return value.toString(); }
}
