package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.exception.RiderNotFoundException;
import com.gresk.modules.rider.domain.exception.RiderNotOwnedException;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.model.valueobject.SubstitutionStatus;
import com.gresk.modules.rider.domain.port.out.RiderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DecideEquipmentSubstitutionUseCase {

    private final RiderRepositoryPort riderRepository;

    @Transactional
    public TechnicalRider execute(String riderId, String promoterId, String lineItemId, boolean approve) {
        TechnicalRider rider = riderRepository.findById(RiderId.of(riderId))
                .orElseThrow(() -> new RiderNotFoundException(riderId));

        if (!rider.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new RiderNotOwnedException(riderId);
        }

        rider.decideLineItemEquivalence(UUID.fromString(lineItemId),
                approve ? SubstitutionStatus.APPROVED : SubstitutionStatus.REJECTED);

        return riderRepository.save(rider);
    }
}
