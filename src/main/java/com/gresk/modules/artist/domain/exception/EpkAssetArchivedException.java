package com.gresk.modules.artist.domain.exception;

public class EpkAssetArchivedException extends RuntimeException {
    public EpkAssetArchivedException(String epkAssetId) {
        super("Cannot add a new version to an archived EPK asset: " + epkAssetId);
    }
}
