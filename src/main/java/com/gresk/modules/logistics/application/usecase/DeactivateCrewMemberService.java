package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.DeactivateCrewMemberCommand;
import com.gresk.modules.logistics.application.port.in.DeactivateCrewMemberUseCase;
import com.gresk.modules.logistics.domain.model.CrewMember;
import com.gresk.modules.logistics.domain.port.out.CrewMemberRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeactivateCrewMemberService implements DeactivateCrewMemberUseCase {

    private final CrewMemberRepositoryPort crewMemberRepository;

    @Override
    public CrewMember execute(DeactivateCrewMemberCommand command) {
        CrewMember crewMember = LogisticsLookups.requireCrewMember(crewMemberRepository, command.crewMemberId(), command.promoterId());
        crewMember.deactivate();
        return crewMemberRepository.save(crewMember);
    }
}
