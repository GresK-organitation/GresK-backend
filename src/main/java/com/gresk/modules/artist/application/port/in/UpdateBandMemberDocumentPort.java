package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.command.UpdateBandMemberDocumentCommand;
import com.gresk.modules.artist.domain.model.BandMember;

public interface UpdateBandMemberDocumentPort {
    BandMember execute(UpdateBandMemberDocumentCommand command);
}
