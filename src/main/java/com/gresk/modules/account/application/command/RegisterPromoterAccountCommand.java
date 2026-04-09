package com.gresk.modules.account.application.command;

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
        String street,
        String city,
        String description,
        Set<String> musicalGenres,
        MultipartFile logo,
        String phone,
        String website
) {
    public Set<Role> roles() {
        return Set.of(Role.PROMOTER_PENDING);
    }
}
