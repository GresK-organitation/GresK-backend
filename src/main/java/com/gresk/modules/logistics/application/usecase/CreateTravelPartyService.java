package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.CreateTravelPartyCommand;
import com.gresk.modules.logistics.application.port.in.CreateTravelPartyUseCase;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.model.TravelParty;
import com.gresk.modules.logistics.domain.port.out.TravelPartyRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateTravelPartyService implements CreateTravelPartyUseCase {

    private final TravelPartyRepositoryPort travelPartyRepository;

    @Override
    public TravelParty execute(CreateTravelPartyCommand command) {
        TravelParty travelParty = TravelParty.create(TourId.of(command.tourId()), PromoterId.of(command.promoterId()));
        return travelPartyRepository.save(travelParty);
    }
}
