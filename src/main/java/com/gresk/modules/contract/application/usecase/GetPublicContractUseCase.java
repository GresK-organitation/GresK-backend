package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.exception.ContractNotFoundException;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPublicContractUseCase {

    private final ContractRepositoryPort contractRepository;

    public Contract execute(String shareToken) {
        return contractRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new ContractNotFoundException("token:" + shareToken));
    }
}
