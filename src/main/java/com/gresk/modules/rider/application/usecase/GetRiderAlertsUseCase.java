package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.model.RiderAlert;
import com.gresk.modules.rider.domain.port.out.RiderAlertRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRiderAlertsUseCase {

    private final RiderAlertRepositoryPort alertRepository;

    @Transactional(readOnly = true)
    public List<RiderAlert> execute(String promoterId) {
        return alertRepository.findUnreadByPromoterId(PromoterId.of(promoterId));
    }
}
