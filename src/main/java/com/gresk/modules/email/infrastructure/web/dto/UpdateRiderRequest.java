package com.gresk.modules.email.infrastructure.web.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public record UpdateRiderRequest(@NotEmpty Map<String, Object> riderData, String notes) {}
