package com.gresk.modules.promoter.infrastructure.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginPromoterRequest(

        @NotBlank @Email
        String email,

        @NotBlank
        String password
) {}
