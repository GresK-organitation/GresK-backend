package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.dto.BandsintownTractionDTO;
import com.gresk.modules.artist.application.port.in.CollectBandsintownTractionPort;
import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.ArtistTractionSnapshot;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.artist.domain.port.out.ArtistTractionSnapshotRepositoryPort;
import com.gresk.modules.artist.domain.port.out.BandsintownArtistTractionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectBandsintownTractionUseCase implements CollectBandsintownTractionPort {

    private final ArtistRepositoryPort                 artistRepository;
    private final BandsintownArtistTractionPort         bandsintownPort;
    private final ArtistTractionSnapshotRepositoryPort  snapshotRepository;

    @Override
    @Transactional
    public void execute() {
        List<Artist> artists = artistRepository.findAll();
        log.info("Bandsintown traction collection: {} artist(s)", artists.size());

        for (Artist artist : artists) {
            try {
                Optional<BandsintownTractionDTO> traction = bandsintownPort.fetchTraction(artist.getName().value());
                traction.ifPresent(dto -> snapshotRepository.save(
                        ArtistTractionSnapshot.create(artist.getId(), dto.followers(), dto.upcomingShows(), null)));
            } catch (Exception e) {
                log.error("Failed to collect Bandsintown traction for artist {}: {}",
                        artist.getId().value(), e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void purgeOlderThan(int retentionDays) {
        snapshotRepository.deleteOlderThan(LocalDate.now().minusDays(retentionDays));
    }
}
