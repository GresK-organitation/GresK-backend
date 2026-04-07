package com.gresk.modules.identity.domain.port.out;

import com.gresk.modules.identity.domain.model.Account;
import com.gresk.modules.identity.domain.model.AccountId;
import com.gresk.shared.domain.valueobject.Email;

import java.util.Optional;

public interface AccountRepositoryPort {

    AccountId save(Account account);

    Optional<Account> findByEmail(Email email);

    boolean existsByEmail(Email email);
}
