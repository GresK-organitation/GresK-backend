package com.gresk.modules.artist.domain.exception;

public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException(String artistId) {
        super("Artist not found: " + artistId);
    }
}
