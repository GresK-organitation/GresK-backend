package com.gresk.modules.promoter.infrastructure.persitence;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface R2dbcPromoterGenreRepository extends ReactiveCrudRepository<R2dbcPromoterGenreEntity, UUID> {

    @Query("SELECT genre FROM promoter_genres WHERE promoter_id = :promoterId")
    Flux<String> findGenresByPromoterId(UUID promoterId);

    @Modifying
    @Query("DELETE FROM promoter_genres WHERE promoter_id = :promoterId")
    Mono<Void> deleteAllByPromoterId(UUID promoterId);

}