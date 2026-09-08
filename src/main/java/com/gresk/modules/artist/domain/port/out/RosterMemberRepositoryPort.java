package com.gresk.modules.artist.domain.port.out;

import com.gresk.modules.artist.domain.model.RosterMember;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.RosterMemberId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface RosterMemberRepositoryPort {
    RosterMember save(RosterMember member);
    Optional<RosterMember> findById(RosterMemberId id);
    Optional<RosterMember> findByIdAndPromoterId(RosterMemberId id, PromoterId promoterId);
    List<RosterMember> findAllByArtistId(ArtistId artistId);
    void deleteById(RosterMemberId id);
}
