package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.query.GetSettlementQuery;
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
@Transactional(readOnly = true)
public class GetSettlementUseCase {

    private final SettlementRepositoryPort settlementRepository;

    public Settlement execute(GetSettlementQuery query) {
        Settlement settlement = settlementRepository.findById(SettlementId.of(query.settlementId()))
                .orElseThrow(() -> new SettlementNotFoundException(query.settlementId()));

        if (!settlement.getPromoterId().equals(PromoterId.of(query.promoterId()))) {
            throw new FinanceResourceNotOwnedException();
        }
        return settlement;
    }
}
