package com.gresk.modules.account.domain.port.out;

import com.gresk.modules.account.domain.model.Account;
import com.gresk.modules.account.domain.model.AccountId;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.user.domain.model.User;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Email;

import java.util.List;
import java.util.Optional;

public interface AccountRepositoryPort {

    Account save(Account account);

    Optional<Account> findByEmail(Email email);

    boolean existsByEmail(Email email);

    Optional<Account> findById(AccountId accountId);

    List<User> findUsersForAdmin(AccountStatus status, City cityFilter);

    List<Promoter> findPromotersForAdmin(AccountStatus status, City cityFilter);
}
