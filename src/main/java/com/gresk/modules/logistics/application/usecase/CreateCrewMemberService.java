package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.CreateCrewMemberCommand;
import com.gresk.modules.logistics.application.port.in.CreateCrewMemberUseCase;
import com.gresk.modules.logistics.domain.model.CrewMember;
import com.gresk.modules.logistics.domain.port.out.CrewMemberRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateCrewMemberService implements CreateCrewMemberUseCase {

    private final CrewMemberRepositoryPort crewMemberRepository;

    @Override
    public CrewMember execute(CreateCrewMemberCommand command) {
        CrewMember crewMember = CrewMember.create(PromoterId.of(command.promoterId()),
                LogisticsInputMapper.toName(command.name()), command.defaultRole(), command.contactPhone(),
                command.contactEmail());
        return crewMemberRepository.save(crewMember);
    }
}
