package com.gresk.modules.account.infrastructure.event;

import java.util.Set;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID accountId,
        String email,
        String name,
        String description,
        String city,
        Set<String> musicGenres,
        String avatarAssetId
) {}