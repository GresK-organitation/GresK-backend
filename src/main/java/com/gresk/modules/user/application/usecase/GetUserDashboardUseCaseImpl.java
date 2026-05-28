package com.gresk.modules.user.application.usecase;

import com.gresk.modules.user.application.dto.EventRecommendedDTO;
import com.gresk.modules.user.application.dto.MusicRecommendedDTO;
import com.gresk.modules.user.application.dto.UserDashboardDTO;
import com.gresk.modules.user.domain.exception.UserNotFoundException;
import com.gresk.modules.user.domain.model.User;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.modules.user.domain.port.in.GetUserDashboardUseCase;
import com.gresk.modules.user.domain.port.out.MusicRecommendationProvider;
import com.gresk.modules.user.domain.port.out.EventRecommendationProvider;
import com.gresk.modules.user.domain.port.out.UserRepositoryPort;
import com.gresk.shared.domain.port.out.ImageUrlResolverPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserDashboardUseCaseImpl implements GetUserDashboardUseCase {

    private final UserRepositoryPort userRepository;
    private final EventRecommendationProvider eventRecommendationProvider;
    private final MusicRecommendationProvider musicRecommendationProvider;
    private final ImageUrlResolverPort imageUrlResolver;

    @Value("${gresk.images.default-url}")
    private String defaultImageUrl;

    @Transactional(readOnly = true)
    @Override
    public UserDashboardDTO execute(UUID id) {
        UserId userId = UserId.of(id);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Set<EventRecommendedDTO> topEvents = eventRecommendationProvider
                .getTopEvents(user.getCity(), user.getMusicGenres())
                .stream()
                .map(domain -> EventRecommendedDTO.fromDomain(domain, defaultImageUrl))
                .collect(Collectors.toSet());

        Set<MusicRecommendedDTO> topTracks = musicRecommendationProvider
                .getSpotifyTopTracks(user.getMusicGenres(), user.getCity().value(), id)
                .stream()
                .map(domain -> MusicRecommendedDTO.fromDomain(domain, defaultImageUrl))
                .collect(Collectors.toSet());

        String avatarUrl = imageUrlResolver.resolveOrDefault(user.getAvatarAssetId());

        return new UserDashboardDTO(
                user.getId().value().toString(),
                user.getName().value(),
                user.getTier().name(),
                user.getCity().value(),
                user.getLoyaltyPoints(),
                avatarUrl,
                user.getMusicGenres().stream().map(Enum::name).toList(),
                topEvents,
                topTracks
        );
    }
}