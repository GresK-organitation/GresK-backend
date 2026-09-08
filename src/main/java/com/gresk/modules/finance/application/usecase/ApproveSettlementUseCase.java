package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.ApproveSettlementCommand;
import com.gresk.modules.finance.application.event.SettlementApprovedEvent;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.exception.SettlementNotFoundException;
import com.gresk.modules.finance.domain.model.Settlement;
import com.gresk.modules.finance.domain.model.SettlementId;
import com.gresk.modules.finance.domain.port.out.SettlementRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ApproveSettlementUseCase {

    private final SettlementRepositoryPort  settlementRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Settlement execute(ApproveSettlementCommand cmd) {
        Settlement settlement = settlementRepository.findById(SettlementId.of(cmd.settlementId()))
                .orElseThrow(() -> new SettlementNotFoundException(cmd.settlementId()));

        if (!settlement.getPromoterId().equals(PromoterId.of(cmd.promoterId()))) {
            throw new FinanceResourceNotOwnedException();
        }

        settlement.approve();
        Settlement saved = settlementRepository.save(settlement);

        eventPublisher.publishEvent(new SettlementApprovedEvent(
                this, saved.getId(), saved.getPromoterId(), saved.getArtistPayableAmount()));

        return saved;
    }
}
