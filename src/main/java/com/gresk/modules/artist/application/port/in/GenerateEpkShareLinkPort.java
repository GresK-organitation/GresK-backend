package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.command.GenerateEpkShareLinkCommand;
import com.gresk.modules.artist.domain.model.EpkShareLink;

public interface GenerateEpkShareLinkPort {
    EpkShareLink execute(GenerateEpkShareLinkCommand command);
}
