package com.gresk.modules.account.application.command;

public record LoginCommand(
        String email,
        String rawPassword
) {}
