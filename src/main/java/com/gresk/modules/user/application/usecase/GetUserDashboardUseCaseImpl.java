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
    public UserDashboardDTO execute(UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<EventRecommendedDTO> topEvents = eventRecommendationProvider
                .getTopEvents(user.getCity(), user.getMusicGenres())
                .stream()
                .map(EventRecommendedDTO::fromDomain)
                .toList();

        List<MusicRecommendedDTO> topTracks = musicRecommendationProvider
                .getSpotifyTopTracks(user.getMusicGenres())
                .stream()
                .map(MusicRecommendedDTO::fromDomain)
                .toList();

        return new UserDashboardDTO(
                user.getId().value().toString(),
                user.getName().value(),
                user.getTier().name(),
                user.getCity().value(),
                user.getLoyaltyPoints(),
                topEvents,
                topTracks
        );
    }
}