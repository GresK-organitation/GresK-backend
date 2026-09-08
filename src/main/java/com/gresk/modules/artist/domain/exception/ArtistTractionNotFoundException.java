package com.gresk.modules.artist.domain.exception;

public class ArtistTractionNotFoundException extends RuntimeException {
    public ArtistTractionNotFoundException(String artistId) {
        super("No traction snapshot found for artist: " + artistId);
    }
}
