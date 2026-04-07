package com.gresk.modules.identity.application.command;

public record LoginCommand(
        String email,
        String rawPassword
) {}
