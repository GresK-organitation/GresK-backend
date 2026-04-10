package com.gresk.modules.artist.application.port.in;

import java.util.UUID;

public interface DeleteArtistPort {
    void execute(String artistId, UUID promoterAccountId);
}
