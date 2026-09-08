package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.command.UpdateRosterMemberCommand;
import com.gresk.modules.artist.domain.model.RosterMember;

public interface UpdateRosterMemberPort {
    RosterMember execute(UpdateRosterMemberCommand command);
}
