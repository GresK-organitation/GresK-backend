package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.exception.RiderNotFoundException;
import com.gresk.modules.rider.domain.exception.RiderNotOwnedException;
import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.model.RiderLineItem;
import com.gresk.modules.rider.domain.model.RiderStatus;
import com.gresk.modules.rider.domain.model.valueobject.EquipmentEquivalence;
import com.gresk.modules.rider.domain.model.valueobject.FulfillmentSource;
import com.gresk.modules.rider.domain.port.out.HospitalityRiderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloneHospitalityRiderUseCase {

    private final HospitalityRiderRepositoryPort riderRepository;

    @Transactional
    public HospitalityRider execute(String riderId, String promoterId) {
        HospitalityRider source = riderRepository.findById(RiderId.of(riderId))
                .orElseThrow(() -> new RiderNotFoundException(riderId));

        if (!source.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new RiderNotOwnedException(riderId);
        }

        Instant now = Instant.now();
        List<RiderLineItem> clonedItems = source.getLineItems().stream()
                .map(item -> RiderLineItem.reconstitute(
                        UUID.randomUUID(), item.getCategory(), item.getDescription(),
                        item.getQuantity(), item.isRequired(), item.getAttributes(),
                        FulfillmentSource.UNRESOLVED, (EquipmentEquivalence) null, item.getNotes()))
                .toList();

        HospitalityRider clone = HospitalityRider.reconstitute(
                RiderId.generate(), source.getArtistId(), source.getPromoterId(),
                source.getName() + " (copia)", RiderStatus.DRAFT, 1,
                clonedItems, source.getAdditionalNotes(), null, now, now);

        return riderRepository.save(clone);
    }
}
