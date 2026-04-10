package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.dto.ArtistResponse;
import com.gresk.modules.artist.application.port.in.GetArtistPort;
import com.gresk.modules.artist.domain.exception.ArtistNotFoundException;
import com.gresk.modules.artist.domain.model.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetArtistUseCase implements GetArtistPort {

    private final ArtistRepositoryPort artistRepository;

    @Override
    @Transactional(readOnly = true)
    public ArtistResponse execute(String artistId) {
        return artistRepository.findById(ArtistId.of(artistId))
                .map(ArtistResponse::from)
                .orElseThrow(() -> new ArtistNotFoundException(artistId));
    }
}
