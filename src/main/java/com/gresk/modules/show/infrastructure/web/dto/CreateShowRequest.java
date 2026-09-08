package com.gresk.modules.show.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record CreateShowRequest(@NotBlank String name, Instant tentativeDate) {}
