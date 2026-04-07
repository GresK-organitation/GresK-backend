package com.gresk.modules.identity.infrastructure.web.dto;

public record AuthResponse(String token, long expiresIn) {}
