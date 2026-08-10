package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.contract.domain.port.out.ContractStats;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetContractStatsUseCase {

    private final ContractRepositoryPort contractRepository;

    public ContractStats execute(String promoterId) {
        return contractRepository.statsForPromoter(PromoterId.of(promoterId));
    }
}
