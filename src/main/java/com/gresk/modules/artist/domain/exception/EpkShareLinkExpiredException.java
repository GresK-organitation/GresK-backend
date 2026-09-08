package com.gresk.modules.artist.domain.exception;

public class EpkShareLinkExpiredException extends RuntimeException {
    public EpkShareLinkExpiredException(String shareLinkId) {
        super("EPK share link expired or exhausted: " + shareLinkId);
    }
}
