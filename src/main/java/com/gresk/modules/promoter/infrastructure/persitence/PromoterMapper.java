package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.Description;
import com.gresk.modules.promoter.domain.valueobject.Location;
import com.gresk.modules.promoter.domain.valueobject.PromoterName;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Email;
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
                entity.getAccountId(),
                new Email(entity.getEmail()),
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
                .accountId(promoter.getAccountId())
                .email(promoter.getEmail().value())
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
