package com.gresk.modules.artist.domain.exception;

public class ArtistAlreadyExistsException extends RuntimeException {
    public ArtistAlreadyExistsException(String contact) {
        super("An artist with this contact already exists for this promoter: " + contact);
    }
}
