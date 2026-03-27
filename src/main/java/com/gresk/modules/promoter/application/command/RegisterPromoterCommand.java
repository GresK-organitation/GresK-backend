package com.gresk.modules.promoter.application.command;

import java.util.List;

public record RegisterPromoterCommand(
        String email,
        String rawPassword,
        String name,
        String city,
        String country,
        String address,
        String description,
        List<String> musicalGenres
        //el command solo contiene datos que transmite el usuario. El resto los gestiona el propiop dominio.
) {
}
