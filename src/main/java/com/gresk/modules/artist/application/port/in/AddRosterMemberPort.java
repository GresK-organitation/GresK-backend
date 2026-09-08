package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.command.AddRosterMemberCommand;
import com.gresk.modules.artist.domain.model.RosterMember;

public interface AddRosterMemberPort {
    RosterMember execute(AddRosterMemberCommand command);
}
