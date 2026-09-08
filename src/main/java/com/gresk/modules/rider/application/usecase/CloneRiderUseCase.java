package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.exception.RiderNotFoundException;
import com.gresk.modules.rider.domain.exception.RiderNotOwnedException;
import com.gresk.modules.rider.domain.model.*;
import com.gresk.modules.rider.domain.model.valueobject.EquipmentEquivalence;
import com.gresk.modules.rider.domain.model.valueobject.FulfillmentSource;
import com.gresk.modules.rider.domain.port.out.RiderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CloneRiderUseCase {

    private final RiderRepositoryPort riderRepository;

    @Transactional
    public TechnicalRider execute(String riderId, String promoterId) {
        TechnicalRider source = riderRepository.findById(RiderId.of(riderId))
                .orElseThrow(() -> new RiderNotFoundException(riderId));

        if (!source.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new RiderNotOwnedException(riderId);
        }

        Instant now = Instant.now();
        List<RiderLineItem> clonedItems = source.getLineItems().stream()
                .map(item -> RiderLineItem.reconstitute(
                        java.util.UUID.randomUUID(), item.getCategory(), item.getDescription(),
                        item.getQuantity(), item.isRequired(), item.getAttributes(),
                        FulfillmentSource.UNRESOLVED, (EquipmentEquivalence) null, item.getNotes()))
                .toList();

        TechnicalRider clone = TechnicalRider.reconstitute(
                RiderId.generate(),
                source.getArtistId(),
                source.getPromoterId(),
                source.getName() + " (copia)",
                RiderStatus.DRAFT,
                1,
                source.getStaff(),
                source.getSoundCheckDurationMinutes(),
                source.getSoundCheckNotes(),
                source.getStageDimensions(),
                source.getStageElements(),
                clonedItems,
                source.getAdditionalNotes(),
                null,
                now,
                now
        );

        return riderRepository.save(clone);
    }
}
