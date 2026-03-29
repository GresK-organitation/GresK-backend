package com.gresk.modules.promoter.application.command;

import java.util.List;

public record UpdatePromoterProfileCommand(
        String promoterId,
        String name,
        String city,
        String country,
        String address,
        String description,
        List<String> musicalGenres
) {}
