package com.gresk.modules.journal.infrastructure.adapter;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.journal.domain.port.out.ArtistLookupPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ArtistLookupAdapter implements ArtistLookupPort {

    private final ArtistRepositoryPort artistRepositoryPort;

    @Override
    public boolean existsById(ArtistId id) {
        return artistRepositoryPort.findById(id).isPresent();
    }

    @Override
    public Optional<String> findNameById(ArtistId id) {
        return artistRepositoryPort.findById(id).map(artist -> artist.getName().value());
    }
}
