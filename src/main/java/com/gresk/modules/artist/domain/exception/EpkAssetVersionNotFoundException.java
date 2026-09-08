package com.gresk.modules.artist.domain.exception;

public class EpkAssetVersionNotFoundException extends RuntimeException {
    public EpkAssetVersionNotFoundException(String epkAssetId, int versionNumber) {
        super("Version " + versionNumber + " not found for EPK asset: " + epkAssetId);
    }
}
