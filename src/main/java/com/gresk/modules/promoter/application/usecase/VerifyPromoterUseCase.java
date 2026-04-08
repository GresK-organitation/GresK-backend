package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.port.in.VerifyPromoterPort;
import com.gresk.modules.promoter.domain.exception.PromoterNotFoundException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.port.out.PromoterRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerifyPromoterUseCase implements VerifyPromoterPort {

    private final PromoterRepositoryPort promoterRepository;

    @Transactional
    @Override
    public void execute(PromoterId id) {
        Promoter promoter = promoterRepository.findById(id)
                .orElseThrow(() -> new PromoterNotFoundException(id.toString()));

        promoter.activate();

        promoterRepository.save(promoter);
    }
}
