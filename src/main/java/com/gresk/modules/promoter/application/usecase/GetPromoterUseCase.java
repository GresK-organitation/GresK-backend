package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.port.in.GetPromoterPort;
import com.gresk.modules.promoter.application.query.GetPromoterQuery;
import com.gresk.modules.promoter.domain.exception.PromoterNotFoundException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.port.out.PromoterRepository;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetPromoterUseCase implements GetPromoterPort {

    private final PromoterRepository promoterRepository;

    @Transactional(readOnly = true)
    @Override
    public Promoter execute(GetPromoterQuery query) {
        PromoterId id = PromoterId.of(query.PromoterId());
        return promoterRepository.findById(id)
                .orElseThrow(() -> new PromoterNotFoundException(query.PromoterId()));
    }
}
