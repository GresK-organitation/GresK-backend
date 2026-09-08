package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.UpdateTravelPartyMembersCommand;
import com.gresk.modules.logistics.application.port.in.UpdateTravelPartyMembersUseCase;
import com.gresk.modules.logistics.domain.model.TravelParty;
import com.gresk.modules.logistics.domain.port.out.TravelPartyRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateTravelPartyMembersService implements UpdateTravelPartyMembersUseCase {

    private final TravelPartyRepositoryPort travelPartyRepository;

    @Override
    public TravelParty execute(UpdateTravelPartyMembersCommand command) {
        TravelParty travelParty = LogisticsLookups.requireTravelPartyById(travelPartyRepository,
                command.travelPartyId(), command.promoterId());
        travelParty.replaceMembers(LogisticsInputMapper.toTravelPartyMembers(command.members()));
        return travelPartyRepository.save(travelParty);
    }
}
