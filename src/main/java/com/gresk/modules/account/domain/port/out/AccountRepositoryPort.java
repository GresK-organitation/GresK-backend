package com.gresk.modules.account.domain.port.out;

import com.gresk.modules.account.domain.model.Account;
import com.gresk.modules.account.domain.model.AccountId;
import com.gresk.shared.domain.valueobject.Email;

import java.util.Optional;

public interface AccountRepositoryPort {

    Account save(Account account);

    Optional<Account> findByEmail(Email email);

    boolean existsByEmail(Email email);

    Optional<Account> findById(AccountId accountId);
}
