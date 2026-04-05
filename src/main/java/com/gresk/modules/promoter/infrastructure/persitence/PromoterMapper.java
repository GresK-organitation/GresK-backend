package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;

@Component
public class PromoterMapper {

    public Promoter toDomain(PromoterEntity entity) {
        Description description = entity.getDescription() != null
                ? new Description(entity.getDescription())
                : null;

        return Promoter.reconstitute(
                PromoterId.of(entity.getId().toString()),
                new Email(entity.getEmail()),
                new Password(entity.getPasswordHash()),
                new PromoterName(entity.getName()),
                description,
                new Location(entity.getCity(), entity.getCountry(), entity.getAddress()),
                new LinkedHashSet<>(entity.getGenres()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.isActive()
        );
    }

    public PromoterEntity toEntity(Promoter promoter) {
        return PromoterEntity.builder()
                .id(promoter.getId().value())
                .email(promoter.getEmail().value())
                .passwordHash(promoter.getPassword().hashedValue())
                .name(promoter.getName().value())
                .description(promoter.getDescription() != null ? promoter.getDescription().value() : null)
                .city(promoter.getLocation().city())
                .country(promoter.getLocation().country())
                .address(promoter.getLocation().address())
                .status(promoter.getStatus())
                .active(promoter.isActive())
                .createdAt(promoter.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .genres(new LinkedHashSet<>(promoter.getMusicalGenres()))
                .build();
    }
}
