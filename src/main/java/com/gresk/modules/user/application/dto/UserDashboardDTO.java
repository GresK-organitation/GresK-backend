package com.gresk.modules.user.application.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record UserDashboardDTO(
        String userId,
        String userName,
        String currentTier,
        String city,
        int loyaltyPoints,
        List<EventRecommendedDTO> recommendedEvents,
        List<MusicRecommendedDTO> recommendedMusic
) {}