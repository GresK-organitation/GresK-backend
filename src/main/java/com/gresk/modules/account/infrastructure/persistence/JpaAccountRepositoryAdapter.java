package com.gresk.modules.account.infrastructure.persistence;

import com.gresk.modules.account.domain.model.Account;
import com.gresk.modules.account.domain.model.AccountId;
import com.gresk.modules.account.domain.port.out.AccountRepositoryPort;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.infrastructure.persitence.PromoterMapper;
import com.gresk.modules.user.domain.model.User;
import com.gresk.modules.user.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaAccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository jpaRepository;
    private final AccountMapper mapper;
    private final PromoterMapper promoterMapper;
    private final UserPersistenceMapper userMapper;

    @Override
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

    @Override
    public List<User> findUsersForAdmin(AccountStatus status, City cityFilter) {
        return jpaRepository.findUsersForAdmin(status, cityFilter != null ? cityFilter.value() : null)
                .stream().map(userMapper::toDomain).toList();
    }

    @Override
    public List<Promoter> findPromotersForAdmin(AccountStatus status, City cityFilter) {
        return jpaRepository.findPromotersForAdmin(status, cityFilter != null ? cityFilter.value() : null)
                .stream().map(promoterMapper::toDomain).toList();
    }

}
