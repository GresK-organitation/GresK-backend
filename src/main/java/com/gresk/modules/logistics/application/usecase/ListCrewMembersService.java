package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.port.in.ListCrewMembersUseCase;
import com.gresk.modules.logistics.application.query.ListCrewMembersQuery;
import com.gresk.modules.logistics.domain.model.CrewMember;
import com.gresk.modules.logistics.domain.port.out.CrewMemberRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListCrewMembersService implements ListCrewMembersUseCase {

    private final CrewMemberRepositoryPort crewMemberRepository;

    @Override
    public List<CrewMember> execute(ListCrewMembersQuery query) {
        return crewMemberRepository.findAllByPromoterId(PromoterId.of(query.promoterId()), query.activeOnly());
    }
}
