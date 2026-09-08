package com.gresk.modules.artist.domain.exception;

public class EpkAssetNotFoundException extends RuntimeException {
    public EpkAssetNotFoundException(String epkAssetId) {
        super("EPK asset not found: " + epkAssetId);
    }
}
