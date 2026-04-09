package com.gresk.shared.domain.event;

import java.util.Set;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String email,
        String name,
        String description,
        String city,
        Set<String> musicGenres,
        String avatarAssetId
) {}