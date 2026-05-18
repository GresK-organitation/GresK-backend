package com.gresk.modules.account.infrastructure.persistence;

import com.gresk.modules.account.domain.model.Account;
import com.gresk.modules.account.domain.model.AccountId;
import com.gresk.modules.account.domain.port.out.AccountRepositoryPort;
import com.gresk.modules.account.infrastructure.persistence.jpa.AccountJpaRepository;
import com.gresk.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaAccountAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository jpaRepository;
    private final AccountMapper mapper;
    @Override
    @Transactional
    public Account save(Account account) {
        AccountEntity entity = jpaRepository.findById(account.getId().value())
                .map(existing -> {
                    existing.updateStatus(account.getStatus());
                    existing.updateRoles(account.getRoles());
                    return existing;
                })
                .orElseGet(() -> mapper.toEntity(account));
        return mapper.toDomain(jpaRepository.save(entity));
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

    @Override
    public Optional<Account> findById(AccountId accountId) {
        return jpaRepository.findById(accountId.value())
                .map(mapper::toDomain);
    }
}
