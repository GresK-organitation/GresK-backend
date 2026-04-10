package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.dto.ArtistResponse;

import java.util.List;
import java.util.UUID;

public interface ListPromoterArtistsPort {
    List<ArtistResponse> execute(UUID promoterAccountId);
}
