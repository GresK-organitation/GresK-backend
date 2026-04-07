package com.gresk.modules.identity.application.command;

import com.gresk.shared.domain.Role;

import java.util.Set;

public record RegisterAccountCommand(
        String email,
        String rawPassword,
        Set<Role> roles
) {}
