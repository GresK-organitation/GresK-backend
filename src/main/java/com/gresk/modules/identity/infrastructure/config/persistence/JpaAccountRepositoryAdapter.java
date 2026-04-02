package com.gresk.modules.identity.infrastructure.config.persistence;

import com.gresk.modules.identity.domain.port.out.AccountRepositoryPort;
import com.gresk.shared.domain.valueobject.Email;
import org.springframework.stereotype.Repository;

@Repository
public class JpaAccountRepositoryAdapter implements AccountRepositoryPort {
    @Override
    public boolean existsByEmail(Email email) {
        return false;
    }
}

