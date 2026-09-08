package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.domain.model.RosterMember;

import java.util.List;

public interface ListRosterMembersPort {
    List<RosterMember> execute(String artistId, String promoterId);
}
