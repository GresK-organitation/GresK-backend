package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.domain.model.ArtistTractionSnapshot;

public interface GetArtistTractionPort {
    ArtistTractionSnapshot execute(String artistId);
}
