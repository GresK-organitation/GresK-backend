package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.command.CreateArtistCommand;
import com.gresk.modules.artist.domain.model.Artist;

public interface CreateArtistPort {
    Artist execute(CreateArtistCommand command);
}
