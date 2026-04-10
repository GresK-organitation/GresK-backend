package com.gresk.modules.artist.domain.exception;

import com.gresk.shared.domain.exception.DomainException;

public class ArtistNotOwnedByPromoterException extends DomainException {
    public ArtistNotOwnedByPromoterException(String artistId) {
        super("Artist " + artistId + " does not belong to the requesting promoter");
    }
}
