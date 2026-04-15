package com.gresk.modules.promoter.application.command;

import java.util.Set;
import java.util.UUID;

public record RegisterPromoterCommand(
        UUID accountId,
        String email,
        String name,
        String street,
        String city,
        String country,
        String description,
        Set<String> musicalGenres,
        String logoAssetId,
        String phone,
        String website
) {}
