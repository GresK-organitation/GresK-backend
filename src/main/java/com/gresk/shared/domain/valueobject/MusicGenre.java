package com.gresk.shared.domain.valueobject;

public enum MusicGenre {
    ROCK, POP, TECHNO, REGGAETON, HIP_HOP, HOUSE,
    INDIE, METAL, TRAP, JAZZ, CLASSICAL, FLAMENCO,
    R_AND_B, PUNK, LATIN_JAZZ, ELECTRONIC, SURPRISE;

    public String key() {
        return "genre." + this.name().toLowerCase();
    }
}