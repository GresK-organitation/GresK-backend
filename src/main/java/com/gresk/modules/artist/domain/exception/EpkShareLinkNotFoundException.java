package com.gresk.modules.artist.domain.exception;

public class EpkShareLinkNotFoundException extends RuntimeException {
    public EpkShareLinkNotFoundException(String token) {
        super("EPK share link not found or invalid token: " + token);
    }
}
