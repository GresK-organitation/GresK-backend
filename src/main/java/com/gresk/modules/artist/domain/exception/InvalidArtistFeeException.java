package com.gresk.modules.artist.domain.exception;

public class InvalidArtistFeeException extends RuntimeException {
    public InvalidArtistFeeException(String message) {
        super(message);
    }
}
