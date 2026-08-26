package com.gresk.modules.discovery.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.port.out.ArtistConnection;
import com.gresk.modules.discovery.domain.port.out.ArtistConnectionsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetArtistConnectionsUseCase {

    private static final int DEFAULT_LIMIT = 6;

    private final ArtistConnectionsPort artistConnectionsPort;

    public List<ArtistConnection> execute(ArtistId artistId) {
        return artistConnectionsPort.findConnections(artistId, DEFAULT_LIMIT);
    }
}
