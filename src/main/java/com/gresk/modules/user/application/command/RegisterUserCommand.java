package com.gresk.modules.user.application.command;

import java.util.List;

public record RegisterUserCommand(
        String email,
        String password,
        String name,
        String description,
        List<String> musicGenres
) {}