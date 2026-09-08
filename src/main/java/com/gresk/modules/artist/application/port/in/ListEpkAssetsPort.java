package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.domain.model.EpkAsset;

import java.util.List;

public interface ListEpkAssetsPort {
    List<EpkAsset> execute(String artistId, String promoterId);
}
