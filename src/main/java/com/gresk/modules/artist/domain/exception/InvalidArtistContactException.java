package com.gresk.modules.artist.domain.exception;

public class InvalidArtistContactException extends RuntimeException {
    public InvalidArtistContactException(String message) {
        super(message);
    }
}
