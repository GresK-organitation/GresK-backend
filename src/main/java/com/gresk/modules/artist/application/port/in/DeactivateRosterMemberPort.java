package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.domain.model.RosterMember;

public interface DeactivateRosterMemberPort {
    RosterMember execute(String rosterMemberId, String promoterId);
}
