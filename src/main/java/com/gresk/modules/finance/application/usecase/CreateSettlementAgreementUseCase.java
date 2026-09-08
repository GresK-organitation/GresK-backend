package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractStatus;
import com.gresk.modules.finance.application.command.CreateSettlementAgreementCommand;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.exception.LinkedContractNotEligibleException;
import com.gresk.modules.finance.domain.exception.SettlementAgreementAlreadyActiveException;
import com.gresk.modules.finance.domain.model.SettlementAgreement;
import com.gresk.modules.finance.domain.model.valueobject.DealTerms;
import com.gresk.modules.finance.domain.port.out.ContractSnapshotProviderPort;
import com.gresk.modules.finance.domain.port.out.SettlementAgreementRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateSettlementAgreementUseCase {

    private final SettlementAgreementRepositoryPort agreementRepository;
    private final ContractSnapshotProviderPort      contractProvider;

    public SettlementAgreement execute(CreateSettlementAgreementCommand cmd) {
        PromoterId promoterId = PromoterId.of(cmd.promoterId());
        UUID linkedContractId = UUID.fromString(cmd.linkedContractId());

        Contract contract = contractProvider.findById(linkedContractId)
                .orElseThrow(() -> new LinkedContractNotEligibleException(
                        "Contract not found: " + cmd.linkedContractId()));

        if (!contract.getPromoterId().equals(promoterId)) {
            throw new FinanceResourceNotOwnedException();
        }
        if (contract.getStatus() != ContractStatus.SIGNED) {
            throw new LinkedContractNotEligibleException(
                    "Contract must be SIGNED to configure a settlement, was: " + contract.getStatus());
        }
        if (contract.getLinkedEventId() == null) {
            throw new LinkedContractNotEligibleException("Contract has no linked event");
        }
        if (agreementRepository.findActiveByLinkedContractId(linkedContractId).isPresent()) {
            throw new SettlementAgreementAlreadyActiveException(cmd.linkedContractId());
        }

        String currency = contract.getFinancialTerms() != null
                ? contract.getFinancialTerms().feeCurrency() : "EUR";

        DealTerms dealTerms = new DealTerms(
                cmd.dealType(),
                cmd.guaranteedAmount() != null ? new Money(cmd.guaranteedAmount(), currency) : null,
                cmd.artistPercentage(),
                cmd.revenueThreshold() != null ? new Money(cmd.revenueThreshold(), currency) : null);

        Money contractFeeSnapshot = contract.getFeeAmount() != null
                ? new Money(contract.getFeeAmount(), currency) : Money.zero(currency);

        var partyB = contract.getPartyB();

        SettlementAgreement agreement = SettlementAgreement.createFromContractSnapshot(
                promoterId, linkedContractId, contract.getLinkedEventId(), dealTerms, contractFeeSnapshot,
                partyB != null ? partyB.name() : null,
                partyB != null ? partyB.taxId() : null,
                partyB != null ? partyB.country() : null,
                partyB == null || partyB.taxResident());

        return agreementRepository.save(agreement);
    }
}
