package com.gresk.modules.identity.domain.port.out;

import com.gresk.shared.domain.valueobject.Email;

public interface AccountRepositoryPort {
    boolean existsByEmail(Email email);
}