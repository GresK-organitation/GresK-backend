package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.exception.RiderNotFoundException;
import com.gresk.modules.rider.domain.model.AlertId;
import com.gresk.modules.rider.domain.model.RiderAlert;
import com.gresk.modules.rider.domain.port.out.RiderAlertRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarkAlertReadUseCase {

    private final RiderAlertRepositoryPort alertRepository;

    @Transactional
    public RiderAlert execute(String alertId, String promoterId) {
        RiderAlert alert = alertRepository.findById(AlertId.of(alertId))
                .orElseThrow(() -> new RiderNotFoundException("Alert not found: " + alertId));

        if (!alert.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new com.gresk.modules.rider.domain.exception.RiderNotOwnedException(alertId);
        }

        alert.markRead();
        return alertRepository.save(alert);
    }
}
