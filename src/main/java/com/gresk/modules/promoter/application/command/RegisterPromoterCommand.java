package com.gresk.modules.promoter.application.command;

import java.util.List;
import java.util.UUID;

public record RegisterPromoterCommand(
        UUID accountId,
        String email,
        String name,
        String city,
        String country,
        String address,
        String description,
        List<String> musicalGenres
) {}
