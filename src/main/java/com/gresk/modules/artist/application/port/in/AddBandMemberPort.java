package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.command.AddBandMemberCommand;
import com.gresk.modules.artist.domain.model.BandMember;

public interface AddBandMemberPort {
    BandMember execute(AddBandMemberCommand command);
}
