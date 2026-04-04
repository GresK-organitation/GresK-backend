package com.gresk.modules.user.application.dto;

import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record UserDashboardDTO(
        String userId,
        String userName,
        String currentTier,
        String city,
        int loyaltyPoints,
        Set<EventRecommendedDTO> recommendedEvents,
        Set<MusicRecommendedDTO> recommendedMusic
) {
}