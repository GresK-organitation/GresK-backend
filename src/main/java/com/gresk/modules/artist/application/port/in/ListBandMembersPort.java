package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.domain.model.BandMember;

import java.util.List;

public interface ListBandMembersPort {
    List<BandMember> execute(String artistId, String promoterId);
}
