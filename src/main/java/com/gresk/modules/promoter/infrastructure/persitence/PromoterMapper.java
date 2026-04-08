package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Address;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.shared.domain.valueobject.Email;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.Name;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PromoterMapper {

    public Promoter toDomain(PromoterEntity entity) {
        return Promoter.reconstitute(
                PromoterId.of(entity.getId().toString()),
                new AssetId(entity.getLogoAssetId()),
                new Email(entity.getEmail()),
                new Name(entity.getName()),
                new Address(entity.getStreet(), City.of(entity.getCity()), entity.getCountry()),
                new Description(entity.getDescription()),
                entity.getGenres(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    public PromoterEntity toEntity(Promoter promoter) {
        return PromoterEntity.builder()
                .id(promoter.getId().value())
                .email(promoter.getEmail().value())
                .name(promoter.getName().value())
                .description(promoter.getDescription().value())
                .logoAssetId(promoter.getLogoAssetId().value())
                .street(promoter.getAddress().street())
                .city(promoter.getAddress().city().value())
                .country(promoter.getAddress().country())
                .status(promoter.getStatus())
                .createdAt(promoter.getCreatedAt())
                .updatedAt(Instant.now())
                .genres(promoter.getMusicalGenres())
                .build();
    }
}
