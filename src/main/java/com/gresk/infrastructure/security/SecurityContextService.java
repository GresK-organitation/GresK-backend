package com.gresk.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityContextService {

    public UUID currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof String principalStr)) {
            throw new UnauthorizedException("Invalid security principal");
        }

        try {
            return UUID.fromString(principalStr);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid principal UUID: " + principalStr);
        }
    }
}
