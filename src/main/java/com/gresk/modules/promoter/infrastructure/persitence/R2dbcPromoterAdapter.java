package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.modules.promoter.domain.MusicGenre;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.Email;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import com.gresk.modules.promoter.port.PromoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class R2dbcPromoterAdapter implements PromoterRepository {

    private final R2dbcPromoterJpaRepository promoterRepo;
    private final R2dbcPromoterGenreRepository genreRepo;


    @Override
    public Mono<Promoter> save(Promoter promoter) {
        UUID id = promoter.getId().value();
        R2dbcPromoterEntity entity = PromoterMapper.toEntity(promoter);
        return promoterRepo.existsById(id)
                .flatMap(exists -> {
                    entity.setNew(!exists);
                    return promoterRepo.save(entity);
                })
                .then(genreRepo.deleteAllByPromoterId(id))
                .thenMany(Flux.fromIterable(promoter.getMusicalGenres())
                        .flatMap(genre -> genreRepo.save(
                                new R2dbcPromoterGenreEntity(id, genre.name())
                        )))
                .then(Mono.just(promoter));
    }

    @Override
    public Mono<Promoter> findById(PromoterId id) {
        UUID uuid = id.value();
        return promoterRepo.findById(uuid)
                .flatMap(entity ->
                        PromoterMapper.toDomain(entity, genreRepo.findGenresByPromoterId(uuid)));
    }

    @Override
    public Mono<Promoter> findByEmail(Email email) {
        return promoterRepo.findByEmail(email.value())
                .flatMap(entity -> PromoterMapper.toDomain(entity, genreRepo.findGenresByPromoterId(entity.getId())));
    }

    @Override
    public Mono<Boolean> existsByEmail(Email email) {
        return promoterRepo.existsByEmail(email.value());
    }

    @Override
    public Flux<Promoter> findAllActive() {
        return promoterRepo.findAllByActiveTrue()
                .flatMap(entity -> PromoterMapper.toDomain(entity ,genreRepo.findGenresByPromoterId(entity.getId())));
    }

    @Override
    public Flux<Promoter> findByGenre(MusicGenre genre) {
        return promoterRepo.findByGenre(genre.name())
                .flatMap(entity -> PromoterMapper.toDomain(entity,
                        genreRepo.findGenresByPromoterId(entity.getId())));
    }
}
