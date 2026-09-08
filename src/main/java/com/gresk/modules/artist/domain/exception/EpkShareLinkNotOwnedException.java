package com.gresk.modules.artist.domain.exception;

public class EpkShareLinkNotOwnedException extends RuntimeException {
    public EpkShareLinkNotOwnedException() {
        super("EPK share link does not belong to this promoter");
    }
}
