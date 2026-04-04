package com.gresk.modules.user.application.command;

import java.util.Set;

public record UpdateUserCommand(
        String name,
        String description,
        String city,
        Set<String> musicGenres
) {}