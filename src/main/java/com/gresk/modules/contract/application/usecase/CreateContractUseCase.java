package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.application.command.CreateContractCommand;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateContractUseCase {

    private final ContractRepositoryPort contractRepository;

    public Contract execute(CreateContractCommand cmd) {
        PromoterId promoterId = PromoterId.of(cmd.promoterId());
        String referenceNumber = generateReferenceNumber(promoterId);

        ContractParty partyA = new ContractParty(
                cmd.partyAName(), cmd.partyATaxId(), cmd.partyAAddress(),
                cmd.partyASignatoryName(), cmd.partyASignatoryRole(), cmd.partyAEmail());

        Contract contract = Contract.create(cmd.type(), promoterId, partyA, referenceNumber);
        return contractRepository.save(contract);
    }

    private String generateReferenceNumber(PromoterId promoterId) {
        int year  = Year.now().getValue();
        int count = contractRepository.countByPromoterIdForYear(promoterId, year);
        return "GRK-" + year + "-" + String.format("%03d", count + 1);
    }
}
