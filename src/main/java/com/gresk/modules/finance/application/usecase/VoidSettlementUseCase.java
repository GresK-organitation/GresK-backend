package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.VoidSettlementCommand;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.exception.SettlementNotFoundException;
import com.gresk.modules.finance.domain.model.Settlement;
import com.gresk.modules.finance.domain.model.SettlementId;
import com.gresk.modules.finance.domain.port.out.SettlementRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VoidSettlementUseCase {

    private final SettlementRepositoryPort settlementRepository;

    public void execute(VoidSettlementCommand cmd) {
        Settlement settlement = settlementRepository.findById(SettlementId.of(cmd.settlementId()))
                .orElseThrow(() -> new SettlementNotFoundException(cmd.settlementId()));

        if (!settlement.getPromoterId().equals(PromoterId.of(cmd.promoterId()))) {
            throw new FinanceResourceNotOwnedException();
        }

        settlement.voidSettlement();
        settlementRepository.save(settlement);
    }
}
