package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.domain.model.Artist;

import java.util.List;

public interface GetArtistsByPromoterPort {
    List<Artist> execute(String promoterId);
}
