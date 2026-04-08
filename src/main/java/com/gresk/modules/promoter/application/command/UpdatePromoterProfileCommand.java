package com.gresk.modules.promoter.application.command;

public record UpdatePromoterProfileCommand(
        String promoterId,
        String name,
        String street,
        String city,
        String country,
        String description,
        java.util.Set<String> musicalGenres
) {}
