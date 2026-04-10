package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.dto.ArtistResponse;
import com.gresk.modules.artist.application.port.in.ListPromoterArtistsPort;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListPromoterArtistsUseCase implements ListPromoterArtistsPort {

    private final ArtistRepositoryPort artistRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ArtistResponse> execute(UUID promoterAccountId) {
        PromoterId promoterId = PromoterId.of(promoterAccountId.toString());
        return artistRepository.findByPromoterId(promoterId)
                .stream()
                .map(ArtistResponse::from)
                .toList();
    }
}
