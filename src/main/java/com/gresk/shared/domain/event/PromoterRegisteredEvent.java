package com.gresk.shared.domain.event;

import java.util.Set;
import java.util.UUID;

public record PromoterRegisteredEvent(
        UUID promoterId,
        String email,
        String companyName,
        String description,
        String street,
        String city,
        String country,
        Set<String> musicalGenres,
        String logoAssetId
) {}
