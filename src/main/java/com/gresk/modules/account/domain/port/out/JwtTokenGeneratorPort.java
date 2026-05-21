package com.gresk.modules.account.domain.port.out;

import com.gresk.infrastructure.port.AuthToken;
import com.gresk.modules.account.domain.model.AccountId;
import com.gresk.shared.domain.Role;
import com.gresk.shared.domain.valueobject.Email;

import java.util.Set;

public interface JwtTokenGeneratorPort {

    AuthToken generate(AccountId accountId, Email email, Set<Role> roles);
}
