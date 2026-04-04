package com.gresk.modules.user.application.command;

import java.util.Set;

public record RegisterUserCommand(
        String email,
        String password,
        String name,
        String description,
        String city,
        Set<String> musicGenres
) {}