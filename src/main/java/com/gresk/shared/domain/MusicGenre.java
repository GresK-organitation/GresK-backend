package com.gresk.shared.domain;

public enum MusicGenre {
    ROCK("rock"),
    POP("pop"),
    TECHNO("techno"),
    REGGAETON("reggaeton"),
    HIP_HOP("hip-hop"),
    HOUSE("house"),
    INDIE("indie"),
    METAL("metal"),
    TRAP("trap"),
    JAZZ("jazz"),
    CLASSICAL("classical"),
    FLAMENCO("flamenco"),
    R_AND_B("r-n-b"),
    PUNK("punk"),
    LATIN_JAZZ("brazil"),
    ELECTRONIC("electronic"),
    SURPRISE("party");

    private final String spotifyKey;

    MusicGenre(String spotifyKey) {
        this.spotifyKey = spotifyKey;
    }

    public String getSpotifyKey() {
        return spotifyKey;
    }

    public String key() {
        return "genre." + this.name().toLowerCase();
    }
}