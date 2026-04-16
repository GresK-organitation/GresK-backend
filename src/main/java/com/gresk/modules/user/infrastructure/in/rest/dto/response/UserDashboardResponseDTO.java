package com.gresk.modules.user.infrastructure.in.rest.dto.response;

import com.gresk.modules.user.application.dto.EventRecommendedDTO;
import com.gresk.modules.user.application.dto.MusicRecommendedDTO;
import com.gresk.modules.user.application.dto.UserDashboardDTO;

import java.util.List;

public record UserDashboardResponseDTO(
        String userId,
        String name,
        String tier,
        int points,
        List<EventResponseDTO> events,
        List<MusicResponseDTO> music
) {
    public record EventResponseDTO(
            String id,
            String title,
            String location,
            String date,
            String time,
            String imageUrl,
            String category,
            String price          // precio efectivo del evento
    ) {
        public static EventResponseDTO from(EventRecommendedDTO dto) {
            return new EventResponseDTO(
                    dto.id(), dto.title(), dto.location(),
                    dto.date(), dto.time(), dto.imageUrl(),
                    dto.category(), dto.price()
            );
        }
    }

    public record MusicResponseDTO(
            String trackName,
            String artistName,
            String spotifyUrl,
            String imageUrl,
            String genre
    ) {
        public static MusicResponseDTO from(MusicRecommendedDTO dto) {
            return new MusicResponseDTO(
                    dto.trackName(), dto.artistName(), dto.spotifyUrl(),
                    dto.imageUrl(), dto.genre().name()
            );
        }
    }

    public static UserDashboardResponseDTO from(UserDashboardDTO dto) {
        return new UserDashboardResponseDTO(
                dto.userId(),
                dto.userName(),
                dto.currentTier(),
                dto.loyaltyPoints(),
                dto.recommendedEvents().stream().map(EventResponseDTO::from).toList(),
                dto.recommendedMusic().stream().map(MusicResponseDTO::from).toList()
        );
    }
}