package com.gresk.modules.identity.infrastructure.persistence;

import com.gresk.modules.identity.domain.model.Account;
import com.gresk.modules.identity.domain.model.AccountId;
import com.gresk.modules.identity.domain.port.out.AccountRepositoryPort;
import com.gresk.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaAccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository jpaRepository;
    private final AccountMapper mapper;

    @Override
    public AccountId save(Account account) {
        AccountEntity entity = mapper.toEntity(account);
        AccountEntity saved = jpaRepository.save(entity);
        return new AccountId(saved.getId());
    }

    @Override
    public Optional<Account> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }
}
