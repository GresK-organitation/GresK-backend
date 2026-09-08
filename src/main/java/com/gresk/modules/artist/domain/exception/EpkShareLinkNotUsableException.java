package com.gresk.modules.artist.domain.exception;

/** Envuelve expired/revoked/exhausted para la capa web cuando basta distinguirlo de un simple 404. */
public class EpkShareLinkNotUsableException extends RuntimeException {
    public EpkShareLinkNotUsableException(String message) {
        super(message);
    }
}
