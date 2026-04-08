package com.gresk.modules.identity.application.command;

import com.gresk.shared.domain.Role;
import lombok.Builder;

import java.util.Set;
@Builder
public record RegisterUserAccountCommand(
        String email,
        String rawPassword,
        String name,
        String description,
        String city,
        Set<String> musicGenres
) {
    public Set<Role> roles() {
        return Set.of(Role.USER);
    }
}