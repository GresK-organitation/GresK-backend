package com.gresk.modules.artist.domain.model.valueobject;

import java.util.List;
import java.util.Objects;

/**
 * Value object que agrupa los datos de vinculación de un artista con Spotify.
 * Es opcional: un artista puede existir en GresK sin estar vinculado a Spotify.
 */
public record SpotifyProfile(
        String       artistId,
        String       spotifyName,
        String       imageUrl,
        List<String> genres
) {

    public SpotifyProfile {
        genres = (genres != null) ? List.copyOf(genres) : List.of();
    }

    public static SpotifyProfile empty() {
        return new SpotifyProfile(null, null, null, List.of());
    }

    public static SpotifyProfile of(String artistId, String spotifyName,
                                    String imageUrl, List<String> genres) {
        return new SpotifyProfile(artistId, spotifyName, imageUrl, genres);
    }

    /** Devuelve true si el artista está vinculado a un perfil de Spotify. */
    public boolean isLinked() {
        return artistId != null && !artistId.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpotifyProfile that)) return false;
        return Objects.equals(artistId, that.artistId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(artistId);
    }
}
