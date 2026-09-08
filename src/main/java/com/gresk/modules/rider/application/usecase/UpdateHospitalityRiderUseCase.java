package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.application.command.UpdateHospitalityRiderCommand;
import com.gresk.modules.rider.domain.exception.RiderNotFoundException;
import com.gresk.modules.rider.domain.exception.RiderNotOwnedException;
import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.port.out.HospitalityRiderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateHospitalityRiderUseCase {

    private final HospitalityRiderRepositoryPort riderRepository;

    @Transactional
    public HospitalityRider execute(UpdateHospitalityRiderCommand command) {
        HospitalityRider rider = riderRepository.findById(RiderId.of(command.riderId()))
                .orElseThrow(() -> new RiderNotFoundException(command.riderId()));

        if (!rider.getPromoterId().equals(PromoterId.of(command.promoterId()))) {
            throw new RiderNotOwnedException(command.riderId());
        }

        boolean changed = false;
        if (command.name() != null) {
            rider.withName(command.name());
            changed = true;
        }
        if (command.additionalNotes() != null) {
            rider.withAdditionalNotes(command.additionalNotes());
            changed = true;
        }

        if (changed) rider.incrementVersion();

        return riderRepository.save(rider);
    }
}
