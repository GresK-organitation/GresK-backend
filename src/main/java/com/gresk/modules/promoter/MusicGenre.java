package com.gresk.modules.promoter;

public enum MusicGenre {
    ROCK("Rock"),
    POP("Pop"),
    TECHNO("Techno"),
    REGGAETON("Reggaetón"),
    HIP_HOP("Hip Hop"),
    HOUSE("House"),
    INDIE("Indie"),
    METAL("Metal"),
    TRAP("Trap"),
    JAZZ("Jazz"),
    CLASSICAL("Clásica"),
    FLAMENCO("Flamenco"),
    R_AND_B("R&B"),
    PUNK("Punk"),
    LATIN_JAZZ("Jazz Latino");

    private final String displayName;

    MusicGenre(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
