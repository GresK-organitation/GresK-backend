package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.modules.promoter.domain.MusicGenre;
import com.gresk.modules.promoter.domain.PromoterStatus;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PromoterMapper {

    private PromoterMapper(){}

    public static Mono<Promoter> toDomain (R2dbcPromoterEntity entity, Flux<String> genres){
        return genres
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .map(genreString -> {
                    Set<MusicGenre> musicGenres = genreString.stream()
                            .map(MusicGenre::valueOf)
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    Description description = entity.getDescription() != null
                            ? new Description(entity.getDescription())
                            : null;

                    return Promoter.reconstitute(
                            PromoterId.of(entity.getId().toString()),
                            new Email(entity.getEmail()),
                            new Password(entity.getPassword_hash()),
                            new PromoterName(entity.getName()),
                            description,
                            new Location(entity.getCity(), entity.getCountry(), entity.getAddress()),
                            musicGenres,
                            PromoterStatus.valueOf(entity.getStatus()),
                            entity.getCreated_at(),
                            entity.isActive()
                    );
                });


    }

    public static R2dbcPromoterEntity toEntity(Promoter promoter) {
        R2dbcPromoterEntity entity = new R2dbcPromoterEntity();
        entity.setId(promoter.getId().value());
        entity.setEmail(promoter.getEmail().value());
        entity.setPassword_hash(promoter.getPassword().hashedValue());
        entity.setName(promoter.getName().value());
        entity.setDescription(promoter.getDescription() != null
                ? promoter.getDescription().value() : null);
        entity.setCity(promoter.getLocation().city());
        entity.setCountry(promoter.getLocation().country());
        entity.setAddress(promoter.getLocation().address());
        entity.setStatus(promoter.getStatus().name());
        entity.setActive(promoter.isActive());
        entity.setCreated_at(promoter.getCreatedAt());
        entity.setUpdated_at(LocalDateTime.now());
        return entity;
    }
}
