package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.GetArtistsByPromoterPort;
import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetArtistsByPromoterUseCase implements GetArtistsByPromoterPort {

    private final ArtistRepositoryPort artistRepository;

    @Override
    public List<Artist> execute(String promoterId) {
        return artistRepository.findAllByPromoterId(PromoterId.of(promoterId));
    }
}
