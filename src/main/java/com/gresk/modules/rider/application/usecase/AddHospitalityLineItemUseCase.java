package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.application.command.AddLineItemCommand;
import com.gresk.modules.rider.domain.exception.RiderNotFoundException;
import com.gresk.modules.rider.domain.exception.RiderNotOwnedException;
import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.model.RiderItemCategory;
import com.gresk.modules.rider.domain.model.RiderLineItem;
import com.gresk.modules.rider.domain.port.out.HospitalityRiderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddHospitalityLineItemUseCase {

    private final HospitalityRiderRepositoryPort riderRepository;

    @Transactional
    public RiderLineItem execute(AddLineItemCommand command) {
        HospitalityRider rider = riderRepository.findById(RiderId.of(command.riderId()))
                .orElseThrow(() -> new RiderNotFoundException(command.riderId()));

        if (!rider.getPromoterId().equals(PromoterId.of(command.promoterId()))) {
            throw new RiderNotOwnedException(command.riderId());
        }

        RiderLineItem item = rider.addLineItem(
                RiderItemCategory.valueOf(command.category()), command.description(), command.quantity(),
                command.required(), command.attributes(), command.notes());
        riderRepository.save(rider);
        return item;
    }
}
