package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.VoidSettlementAgreementCommand;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.exception.InvalidSettlementStatusTransitionException;
import com.gresk.modules.finance.domain.exception.SettlementAgreementNotFoundException;
import com.gresk.modules.finance.domain.model.Settlement;
import com.gresk.modules.finance.domain.model.SettlementAgreement;
import com.gresk.modules.finance.domain.model.SettlementAgreementId;
import com.gresk.modules.finance.domain.model.SettlementStatus;
import com.gresk.modules.finance.domain.port.out.SettlementAgreementRepositoryPort;
import com.gresk.modules.finance.domain.port.out.SettlementRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VoidSettlementAgreementUseCase {

    private final SettlementAgreementRepositoryPort agreementRepository;
    private final SettlementRepositoryPort          settlementRepository;

    public void execute(VoidSettlementAgreementCommand cmd) {
        SettlementAgreementId agreementId = SettlementAgreementId.of(cmd.agreementId());
        SettlementAgreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new SettlementAgreementNotFoundException(cmd.agreementId()));

        if (!agreement.getPromoterId().equals(PromoterId.of(cmd.promoterId()))) {
            throw new FinanceResourceNotOwnedException();
        }

        boolean hasSettledPayment = settlementRepository.findBySettlementAgreementId(agreementId).stream()
                .map(Settlement::getStatus)
                .anyMatch(status -> status == SettlementStatus.APPROVED || status == SettlementStatus.PAID);
        if (hasSettledPayment) {
            throw new InvalidSettlementStatusTransitionException(
                    "Cannot void a settlement agreement with an approved or paid settlement");
        }

        agreement.voidAgreement();
        agreementRepository.save(agreement);
    }
}
