package com.gresk.modules.discovery.infrastructure.persistence.mapper;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.model.DemandSignal;
import com.gresk.modules.discovery.domain.model.DemandSignalId;
import com.gresk.modules.discovery.infrastructure.persistence.entity.DemandSignalEntity;
import com.gresk.modules.user.domain.model.UserId;
import org.springframework.stereotype.Component;

@Component
public class DemandSignalMapper {

    public DemandSignal toDomain(DemandSignalEntity e) {
        return DemandSignal.reconstitute(
                DemandSignalId.of(e.getId()),
                ArtistId.of(e.getArtistId()),
                UserId.of(e.getUserId()),
                e.getCity(),
                e.getCreatedAt()
        );
    }

    public DemandSignalEntity toEntity(DemandSignal s) {
        return DemandSignalEntity.builder()
                .id(s.getId().value())
                .artistId(s.getArtistId().value())
                .userId(s.getUserId().value())
                .city(s.getCity())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
