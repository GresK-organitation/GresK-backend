package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.exception.RiderNotFoundException;
import com.gresk.modules.rider.domain.exception.RiderNotOwnedException;
import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.port.out.HospitalityRiderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RemoveHospitalityLineItemUseCase {

    private final HospitalityRiderRepositoryPort riderRepository;

    @Transactional
    public void execute(String riderId, String promoterId, String lineItemId) {
        HospitalityRider rider = riderRepository.findById(RiderId.of(riderId))
                .orElseThrow(() -> new RiderNotFoundException(riderId));

        if (!rider.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new RiderNotOwnedException(riderId);
        }

        rider.removeLineItem(UUID.fromString(lineItemId));
        riderRepository.save(rider);
    }
}
