package com.gresk.modules.discovery.application.usecase;

import com.gresk.modules.discovery.domain.model.SizeTier;
import com.gresk.modules.discovery.domain.port.out.DiscoveryFilters;
import com.gresk.modules.discovery.domain.port.out.DiscoverySortOption;
import com.gresk.modules.discovery.domain.port.out.LiveFilter;
import com.gresk.modules.discovery.domain.port.out.UserGenreAffinityPort;
import com.gresk.modules.discovery.domain.port.out.UserProfilePort;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.MusicGenre;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * "Sorpréndeme": lee el ADN Musical del usuario (dimensión Diversidad, vía
 * top géneros) y arma un filtro que excluye sus géneros habituales,
 * prioriza artistas pequeños y con concierto próximo cerca de él.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildSurpriseMeFiltersUseCase {

    private static final int TOP_GENRES_TO_EXCLUDE = 3;

    private final UserGenreAffinityPort userGenreAffinityPort;
    private final UserProfilePort userProfilePort;

    public DiscoveryFilters execute(UserId userId) {
        Set<MusicGenre> excludeGenres = Set.copyOf(
                userGenreAffinityPort.findTopGenres(userId, TOP_GENRES_TO_EXCLUDE).topGenres());
        Optional<String> city = userProfilePort.findCityByUserId(userId);

        return new DiscoveryFilters(
                Set.of(SizeTier.MICRO, SizeTier.SMALL),
                city,
                Optional.empty(),
                Set.of(),
                excludeGenres,
                Optional.of(LiveFilter.THIS_MONTH),
                city,
                false, false, Optional.empty(),
                false, false,
                DiscoverySortOption.GRESK_SCORE,
                0, 20
        );
    }
}
