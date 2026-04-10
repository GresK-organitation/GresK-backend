package com.gresk.modules.artist.domain.exception;

import com.gresk.shared.domain.exception.DomainException;

public class ArtistNotFoundException extends DomainException {
    public ArtistNotFoundException(String id) {
        super("Artist not found: " + id);
    }
}
