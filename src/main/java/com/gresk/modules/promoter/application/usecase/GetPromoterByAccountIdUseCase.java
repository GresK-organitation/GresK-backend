package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.port.in.GetPromoterByAccountIdPort;
import com.gresk.modules.promoter.domain.exception.PromoterNotFoundException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.port.out.PromoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPromoterByAccountIdUseCase implements GetPromoterByAccountIdPort {

    private final PromoterRepository promoterRepository;

    @Transactional(readOnly = true)
    @Override
    public Promoter execute(UUID accountId) {
        return promoterRepository.findByAccountId(accountId)
                .orElseThrow(() -> new PromoterNotFoundException(accountId.toString()));
    }
}
