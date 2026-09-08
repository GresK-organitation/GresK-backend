package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.application.command.ProposeEquipmentSubstitutionCommand;
import com.gresk.modules.rider.domain.exception.RiderNotFoundException;
import com.gresk.modules.rider.domain.exception.RiderNotOwnedException;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.model.valueobject.EquipmentEquivalence;
import com.gresk.modules.rider.domain.port.out.RiderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProposeEquipmentSubstitutionUseCase {

    private final RiderRepositoryPort riderRepository;

    @Transactional
    public TechnicalRider execute(ProposeEquipmentSubstitutionCommand command) {
        TechnicalRider rider = riderRepository.findById(RiderId.of(command.riderId()))
                .orElseThrow(() -> new RiderNotFoundException(command.riderId()));

        if (!rider.getPromoterId().equals(PromoterId.of(command.promoterId()))) {
            throw new RiderNotOwnedException(command.riderId());
        }

        rider.proposeLineItemEquivalence(
                UUID.fromString(command.lineItemId()),
                command.proposedAlternative(),
                EquipmentEquivalence.ProposedBy.valueOf(command.proposedBy()),
                command.notes());

        return riderRepository.save(rider);
    }
}
