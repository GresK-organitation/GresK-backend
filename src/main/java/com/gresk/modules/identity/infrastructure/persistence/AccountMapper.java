package com.gresk.modules.identity.infrastructure.persistence;

import com.gresk.modules.identity.domain.model.Account;
import com.gresk.modules.identity.domain.model.AccountId;
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
                new AccountId(entity.getId()),
                new Email(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getRoles(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
