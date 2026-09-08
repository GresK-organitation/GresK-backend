package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.IdentityDocument;
import com.gresk.modules.logistics.application.command.UpdateCrewMemberCommand;
import com.gresk.modules.logistics.application.port.in.UpdateCrewMemberUseCase;
import com.gresk.modules.logistics.domain.model.CrewMember;
import com.gresk.modules.logistics.domain.port.out.CrewMemberRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCrewMemberService implements UpdateCrewMemberUseCase {

    private final CrewMemberRepositoryPort crewMemberRepository;

    @Override
    public CrewMember execute(UpdateCrewMemberCommand command) {
        CrewMember crewMember = LogisticsLookups.requireCrewMember(crewMemberRepository, command.crewMemberId(), command.promoterId());
        crewMember.updateProfile(LogisticsInputMapper.toName(command.name()), command.defaultRole(),
                command.contactPhone(), command.contactEmail());
        for (IdentityDocument document : LogisticsInputMapper.toIdentityDocuments(command.documents())) {
            crewMember.addOrRenewDocument(document);
        }
        return crewMemberRepository.save(crewMember);
    }
}
