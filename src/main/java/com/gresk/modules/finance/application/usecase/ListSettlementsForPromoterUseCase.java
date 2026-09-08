package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.query.ListSettlementsQuery;
import com.gresk.modules.finance.domain.model.Settlement;
import com.gresk.modules.finance.domain.port.out.SettlementRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListSettlementsForPromoterUseCase {

    private final SettlementRepositoryPort settlementRepository;

    public List<Settlement> execute(ListSettlementsQuery query) {
        return settlementRepository.findByPromoterId(PromoterId.of(query.promoterId()));
    }
}
