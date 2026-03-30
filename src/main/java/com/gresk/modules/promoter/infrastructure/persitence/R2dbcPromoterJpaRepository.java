package com.gresk.modules.promoter.infrastructure.persitence;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface R2dbcPromoterJpaRepository extends ReactiveCrudRepository<R2dbcPromoterEntity, UUID>{

    Mono<R2dbcPromoterEntity> findByEmail (String email);

    Mono<Boolean> existsByEmail (String email);

    Flux<R2dbcPromoterEntity> findAllByActiveTrue ();

    @Query("SELECT p.* FROM promoters p " +
                  "INNER JOIN promoter_genres pg ON p.id = pg.promoter_id " +
                  "WHERE pg.genre = :genre")
    Flux<R2dbcPromoterEntity> findByGenre(String genre);
}
