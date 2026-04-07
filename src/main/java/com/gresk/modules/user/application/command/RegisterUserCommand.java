package com.gresk.modules.user.application.command;

import java.util.Set;
import java.util.UUID;

public record RegisterUserCommand(
        String email,
        String name,
        String description,
        String city,
        Set<String> musicGenres
) {}
