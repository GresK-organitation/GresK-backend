package com.gresk.modules.account.infrastructure.persistence;

import com.gresk.modules.account.domain.exception.AccountNotFoundException;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.port.out.AccountStatusPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccountStatusAdapter implements AccountStatusPort {

    private final AccountJpaRepository jpaRepository;

    @Override
    public AccountStatus getStatus(UUID accountId) {
        return jpaRepository.findById(accountId)
                .map(AccountEntity::getStatus)
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));
    }
}
