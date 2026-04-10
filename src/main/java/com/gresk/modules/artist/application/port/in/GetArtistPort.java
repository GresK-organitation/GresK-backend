package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.dto.ArtistResponse;

public interface GetArtistPort {
    ArtistResponse execute(String artistId);
}
