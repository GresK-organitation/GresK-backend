package com.gresk.modules.identity.application.command;

import com.gresk.shared.domain.Role;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Builder
public record RegisterPromoterAccountCommand(
        String email,
        String rawPassword,
        String companyName,
        String country,
        String address,
        String city,
        String description,
        Set<String> musicalGenres,
        MultipartFile logo
) {
    public Set<Role> roles() {
        return Set.of(Role.PROMOTER_PENDING);
    }
}
