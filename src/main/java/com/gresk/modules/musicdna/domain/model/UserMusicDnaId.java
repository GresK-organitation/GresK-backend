package com.gresk.modules.musicdna.domain.model;

import java.util.UUID;

public record UserMusicDnaId(UUID value) {

    public UserMusicDnaId {
        if (value == null) throw new IllegalArgumentException("UserMusicDnaId must not be null");
    }

    public static UserMusicDnaId generate() { return new UserMusicDnaId(UUID.randomUUID()); }

    public static UserMusicDnaId of(UUID value) { return new UserMusicDnaId(value); }

    public static UserMusicDnaId of(String value) {
        try { return new UserMusicDnaId(UUID.fromString(value)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserMusicDnaId: " + value);
        }
    }

    @Override
    public String toString() { return value.toString(); }
}
