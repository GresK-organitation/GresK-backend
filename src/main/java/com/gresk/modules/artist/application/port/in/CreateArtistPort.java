package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.command.CreateArtistCommand;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;

public interface CreateArtistPort {
    ArtistId execute(CreateArtistCommand command);
}
