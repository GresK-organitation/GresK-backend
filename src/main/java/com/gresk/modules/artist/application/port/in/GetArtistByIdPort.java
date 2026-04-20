package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.domain.model.Artist;

public interface GetArtistByIdPort {
    Artist execute(String artistId, String promoterId);
}
