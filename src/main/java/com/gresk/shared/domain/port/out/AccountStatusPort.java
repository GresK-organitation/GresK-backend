package com.gresk.shared.domain.port.out;

import com.gresk.shared.domain.AccountStatus;

import java.util.UUID;

public interface AccountStatusPort {
    AccountStatus getStatus(UUID accountId);
}
