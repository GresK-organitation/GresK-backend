package com.gresk.modules.promoter.domain.model;

import com.gresk.modules.promoter.domain.valueObjects.*;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public final class Promoter {

    private final PromoterId id;
    private final PromoterName name;
    private final Email email;
    private final Password password;
    private final Description description;
    private final Location location;
    private final LocalDateTime creationDate;

    public static Promoter create (PromoterId id, PromoterName name, Email email, Password password, Description description, Location location, LocalDateTime creationDate) {
        return Promoter.builder()
                .id(id)
                .name(name)
                .email(email)
                .password(password)
                .description(description)
                .location(location)
                .creationDate(creationDate)
                .build();
    }
}
