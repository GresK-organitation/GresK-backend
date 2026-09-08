package com.gresk.modules.artist.infrastructure.persistence.mapper;

import com.gresk.modules.artist.domain.model.ArtistTractionSnapshot;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.CityAudience;
import com.gresk.modules.artist.domain.model.valueobject.GeographicTraction;
import com.gresk.modules.artist.infrastructure.persistence.entity.ArtistTractionSnapshotEntity;
import com.gresk.modules.artist.infrastructure.persistence.entity.CityAudienceEmbeddable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ArtistTractionSnapshotMapper {

    public ArtistTractionSnapshot toDomain(ArtistTractionSnapshotEntity e) {
        List<CityAudience> entries = e.getCityAudience().stream()
                .map(c -> new CityAudience(c.getCity(), c.getCountry(), c.getAudienceScore(), c.getSource()))
                .toList();

        return new ArtistTractionSnapshot(
                e.getId(),
                ArtistId.of(e.getArtistId()),
                e.getSnapshotDate(),
                e.getBandsintownFollowers(),
                e.getBandsintownUpcomingShows(),
                new GeographicTraction(entries),
                e.getCreatedAt()
        );
    }

    public ArtistTractionSnapshotEntity toEntity(ArtistTractionSnapshot s) {
        List<CityAudienceEmbeddable> entries = new ArrayList<>();
        for (CityAudience c : s.geographicTraction().entries()) {
            entries.add(CityAudienceEmbeddable.builder()
                    .city(c.city())
                    .country(c.country())
                    .audienceScore(c.audienceScore())
                    .source(c.source())
                    .build());
        }

        return ArtistTractionSnapshotEntity.builder()
                .id(s.id())
                .artistId(s.artistId().value())
                .snapshotDate(s.snapshotDate())
                .bandsintownFollowers(s.bandsintownFollowers())
                .bandsintownUpcomingShows(s.bandsintownUpcomingShows())
                .cityAudience(entries)
                .createdAt(s.createdAt())
                .build();
    }
}
