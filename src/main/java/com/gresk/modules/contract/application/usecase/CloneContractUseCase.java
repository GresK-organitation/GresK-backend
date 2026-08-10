package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.exception.ContractNotFoundException;
import com.gresk.modules.contract.domain.exception.ContractNotOwnedException;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.ContractStatus;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Year;

@Service
@RequiredArgsConstructor
@Transactional
public class CloneContractUseCase {

    private final ContractRepositoryPort contractRepository;

    public Contract execute(String contractId, String promoterId) {
        Contract original = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new ContractNotFoundException(contractId));
        if (!original.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new ContractNotOwnedException();
        }

        PromoterId pid = original.getPromoterId();
        int year = Year.now().getValue();
        int count = contractRepository.countByPromoterIdForYear(pid, year);
        String newRef = "GRK-" + year + "-" + String.format("%03d", count + 1);

        Instant now = Instant.now();
        Contract cloned = Contract.reconstitute(
                ContractId.generate(),
                pid,
                original.getType(),
                newRef,
                ContractStatus.DRAFT,
                original.getPartyA(),
                original.getPartyB(),
                original.getPerformanceDetails(),
                original.getFinancialTerms(),
                original.getClauses(),
                original.getJurisdiction(),
                original.getContractCity(),
                original.getContractDate(),
                original.getLinkedEventId(),
                original.getLinkedArtistId(),
                original.getLinkedRiderId(),
                null,   // signedPdfAssetId reset
                null,   // shareToken reset
                now, now
        );

        return contractRepository.save(cloned);
    }
}
