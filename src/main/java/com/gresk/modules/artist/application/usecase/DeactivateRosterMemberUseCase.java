package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.DeactivateRosterMemberPort;
import com.gresk.modules.artist.domain.exception.RosterMemberNotFoundException;
import com.gresk.modules.artist.domain.model.RosterMember;
import com.gresk.modules.artist.domain.model.valueobject.RosterMemberId;
import com.gresk.modules.artist.domain.port.out.RosterMemberRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeactivateRosterMemberUseCase implements DeactivateRosterMemberPort {

    private final RosterMemberRepositoryPort rosterMemberRepository;

    @Override
    public RosterMember execute(String rosterMemberId, String promoterId) {
        RosterMember member = rosterMemberRepository
                .findByIdAndPromoterId(RosterMemberId.of(rosterMemberId), PromoterId.of(promoterId))
                .orElseThrow(() -> new RosterMemberNotFoundException(rosterMemberId));
        member.deactivate();
        return rosterMemberRepository.save(member);
    }
}
