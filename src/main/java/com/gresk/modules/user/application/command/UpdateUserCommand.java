package com.gresk.modules.user.application.command;

import java.util.List;

public record UpdateUserCommand(
        String userId,
        String name,
        String description,
        String city,
        List<String> musicGenres
) {}