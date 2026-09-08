package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.application.command.UpdateContractCommand;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.valueobject.*;
import com.gresk.modules.contract.domain.exception.ContractNotFoundException;
import com.gresk.modules.contract.domain.exception.ContractNotOwnedException;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateContractUseCase {

    private final ContractRepositoryPort contractRepository;

    public Contract execute(UpdateContractCommand cmd) {
        Contract contract = contractRepository.findById(ContractId.of(cmd.contractId()))
                .orElseThrow(() -> new ContractNotFoundException(cmd.contractId()));

        if (!contract.getPromoterId().equals(PromoterId.of(cmd.promoterId()))) {
            throw new ContractNotOwnedException();
        }

        if (cmd.partyBName() != null) {
            contract.withPartyB(new ContractParty(
                    cmd.partyBName(), cmd.partyBTaxId(), cmd.partyBAddress(),
                    cmd.partyBSignatoryName(), cmd.partyBSignatoryRole(), cmd.partyBEmail(),
                    cmd.partyBCountry(), cmd.partyBTaxResident() == null || cmd.partyBTaxResident()));
        }

        if (cmd.perfVenue() != null || cmd.perfEventDate() != null) {
            contract.withPerformanceDetails(new PerformanceDetails(
                    cmd.perfVenue(), cmd.perfEventDate(),
                    cmd.perfDurationMinutes(), cmd.perfShowTime()));
        }

        if (cmd.feeAmount() != null) {
            List<PaymentTerm> terms = cmd.paymentTerms() == null ? List.of() :
                    cmd.paymentTerms().stream()
                            .map(p -> new PaymentTerm(p.percentage(), p.description(), p.method(), p.paid()))
                            .toList();
            WithholdingTax wht = cmd.withholdingTax() != null
                    ? new WithholdingTax(WithholdingTaxType.valueOf(cmd.withholdingTax().type()),
                        cmd.withholdingTax().ratePercentage(), cmd.withholdingTax().taxBase(),
                        cmd.withholdingTax().withheldAmount(), cmd.withholdingTax().exemptionReason())
                    : null;
            contract.withFinancialTerms(new FinancialTerms(
                    cmd.feeAmount(),
                    cmd.feeCurrency() != null ? cmd.feeCurrency() : "EUR",
                    terms, wht));
        }

        if (cmd.clauses() != null) {
            List<ContractClause> clauses = cmd.clauses().stream()
                    .map(c -> new ContractClause(c.order(), c.title(), c.content()))
                    .toList();
            contract.withClauses(clauses);
        }

        if (cmd.jurisdiction()  != null) contract.withJurisdiction(cmd.jurisdiction());
        if (cmd.contractCity()  != null) contract.withContractCity(cmd.contractCity());
        if (cmd.contractDate()  != null) contract.withContractDate(cmd.contractDate());
        if (cmd.linkedEventId()  != null) contract.withLinkedEventId(cmd.linkedEventId());
        if (cmd.linkedArtistId() != null) contract.withLinkedArtistId(cmd.linkedArtistId());
        if (cmd.linkedRiderId()  != null) contract.withLinkedRiderId(cmd.linkedRiderId());

        return contractRepository.save(contract);
    }
}
