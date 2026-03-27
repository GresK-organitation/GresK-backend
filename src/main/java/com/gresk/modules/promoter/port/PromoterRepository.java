package com.gresk.modules.promoter.port;

import com.gresk.modules.promoter.MusicGenre;
import com.gresk.modules.promoter.model.Promoter;
import com.gresk.modules.promoter.valueObjects.Email;
import com.gresk.modules.promoter.valueObjects.PromoterId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PromoterRepository {

    Mono<Promoter> save (Promoter promoter);
    Mono<Promoter> findById(PromoterId id);
    Mono<Promoter> findByEmail(Email email);
    Mono<Boolean> existsByEmail(Email email);
    Flux<Promoter> findAllActive();
    Flux<Promoter> findByGenre(MusicGenre genre);
}
