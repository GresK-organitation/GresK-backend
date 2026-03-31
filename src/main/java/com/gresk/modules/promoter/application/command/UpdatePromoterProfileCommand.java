package com.gresk.modules.promoter.application.command;

import java.util.List;
import java.util.Set;

public record UpdatePromoterProfileCommand(
        String promoterId,
        String name,
        String city,
        String country,
        String address,
        String description,
        Set<String> musicalGenres
) {}
