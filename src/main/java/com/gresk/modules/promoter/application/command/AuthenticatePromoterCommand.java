package com.gresk.modules.promoter.application.command;

public record AuthenticatePromoterCommand(
        String email,
        String rawPassword
) {}
