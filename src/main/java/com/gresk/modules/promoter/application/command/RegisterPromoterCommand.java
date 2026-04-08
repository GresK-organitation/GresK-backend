package com.gresk.modules.promoter.application.command;

public record RegisterPromoterCommand(
        String email,
        String name,
        String street,
        String city,
        String country,
        String description
) {}
