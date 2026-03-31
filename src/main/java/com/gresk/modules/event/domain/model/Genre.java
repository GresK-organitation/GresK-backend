package com.gresk.modules.event.domain.model;

public enum Genre {
    ELECTRONIC("Electronic"),
    JAZZ("Jazz"),
    ROCK("Rock"),
    INDIE("Indie"),
    HIP_HOP("Hip Hop"),
    CLASSICAL("Clásica"),
    FLAMENCO("Flamenco"),
    POP("Pop"),
    REGGAETON("Reggaetón"),
    SURPRISE("Surprise");

    private final String displayName;

    Genre(String displayName) {
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
