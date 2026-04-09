package com.gresk.modules.user.application.command;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;
@Builder
public record RegisterUserCommand(
        UUID userId,
        String email,
        String name,
        String description,
        String city,
        Set<String> musicGenres,
        String avatarAssetId
) {}
