package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractStatus;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPromoterContractsUseCase {

    private final ContractRepositoryPort contractRepository;

    public List<Contract> execute(String promoterId, ContractStatus status, ContractType type) {
        PromoterId pid = PromoterId.of(promoterId);
        if (status != null) return contractRepository.findByPromoterIdAndStatus(pid, status);
        if (type   != null) return contractRepository.findByPromoterIdAndType(pid, type);
        return contractRepository.findByPromoterId(pid);
    }
}
