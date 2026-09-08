package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.port.in.GetCrewMemberUseCase;
import com.gresk.modules.logistics.application.query.GetCrewMemberQuery;
import com.gresk.modules.logistics.domain.model.CrewMember;
import com.gresk.modules.logistics.domain.port.out.CrewMemberRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCrewMemberService implements GetCrewMemberUseCase {

    private final CrewMemberRepositoryPort crewMemberRepository;

    @Override
    public CrewMember execute(GetCrewMemberQuery query) {
        return LogisticsLookups.requireCrewMember(crewMemberRepository, query.crewMemberId(), query.promoterId());
    }
}
