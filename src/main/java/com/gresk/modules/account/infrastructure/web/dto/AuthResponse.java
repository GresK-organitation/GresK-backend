package com.gresk.modules.account.infrastructure.web.dto;

public record AuthResponse(String token, long expiresIn) {}
