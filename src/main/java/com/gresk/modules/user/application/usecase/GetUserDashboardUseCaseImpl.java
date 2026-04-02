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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserDashboardUseCaseImpl implements GetUserDashboardUseCase {

    private final UserRepositoryPort userRepository;
    private final EventRecommendationProvider eventRecommendationProvider;
    private final MusicRecommendationProvider musicRecommendationProvider;

    @Override
    @Transactional(readOnly = true)
    public UserDashboardDTO execute(UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<EventRecommendedDTO> topEvents = eventRecommendationProvider.getTopEventsForUser(user.getId()).stream()
                .map(EventRecommendedDTO::fromDomain)
                .toList();

        List<MusicRecommendedDTO> topTracks = musicRecommendationProvider.getSpotifyTopTracks(user.getId()).stream()
                .map(MusicRecommendedDTO::fromDomain)
                .toList();

        return UserDashboardDTO.builder()
                .userId(user.getId().toString())
                .userName(user.getName().toString())
                .currentTier(user.getTier().name())
                .loyaltyPoints(user.getLoyaltyPoints())
                .recommendedEvents(topEvents)
                .recommendedMusic(topTracks)
                .build();
    }
}
