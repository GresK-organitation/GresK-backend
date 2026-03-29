package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.query.GetPromoterQuery;
import com.gresk.modules.promoter.domain.exception.PromoterNotFoundException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import com.gresk.modules.promoter.port.PromoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetPromoterUseCase {

    private final PromoterRepository promoterRepository;

    public Mono<Promoter> execute (GetPromoterQuery query){
        return Mono.defer(() -> {
            PromoterId id = PromoterId.of(query.PromoterId());
            return promoterRepository.findById(id)
                    .switchIfEmpty(Mono.error(new PromoterNotFoundException(query.PromoterId())));
        });
    }


}
