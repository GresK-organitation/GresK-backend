package com.gresk.modules.discovery.infrastructure.adapter;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.port.out.ArtistConnection;
import com.gresk.modules.discovery.domain.port.out.ArtistConnectionsPort;
import com.gresk.modules.discovery.infrastructure.persistence.ArtistConnectionsQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ArtistConnectionsAdapter implements ArtistConnectionsPort {

    private final ArtistConnectionsQueryRepository queryRepository;

    @Override
    public List<ArtistConnection> findConnections(ArtistId artistId, int limit) {
        return queryRepository.findConnections(artistId.value(), limit).stream()
                .map(row -> new ArtistConnection(row.getArtistId(), row.getName(), row.getImageAssetId(),
                        row.getCoReviewCount() != null ? row.getCoReviewCount() : 0))
                .toList();
    }
}
