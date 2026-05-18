package com.gresk.modules.account.infrastructure.persistence;

import com.gresk.modules.account.domain.model.Account;
import com.gresk.modules.account.domain.model.AccountId;
import com.gresk.shared.domain.valueobject.Email;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountEntity toEntity(Account account) {
        return AccountEntity.builder()
                .id(account.getId().value())
                .email(account.getEmail().value())
                .passwordHash(account.getPasswordHash())
                .status(account.getStatus())
                .roles(account.getRoles())
                .createdAt(account.getCreatedAt())
                .build();
    }

    public Account toDomain(AccountEntity entity) {
        return Account.reconstitute(
                AccountId.of(entity.getId()),
                Email.of(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getRoles(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
