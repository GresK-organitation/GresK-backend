package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.command.RegisterArtistCommand;
import com.gresk.modules.artist.application.dto.ArtistResponse;

public interface RegisterArtistPort {
    ArtistResponse execute(RegisterArtistCommand command);
}
