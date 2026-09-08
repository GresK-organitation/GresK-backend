package com.gresk.modules.artist.domain.exception;

public class EpkShareLinkRevokedException extends RuntimeException {
    public EpkShareLinkRevokedException(String shareLinkId) {
        super("EPK share link has been revoked: " + shareLinkId);
    }
}
