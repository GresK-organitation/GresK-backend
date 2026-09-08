package com.gresk.modules.artist.application.port.in;

public interface ArchiveEpkAssetPort {
    void execute(String epkAssetId, String promoterId);
}
