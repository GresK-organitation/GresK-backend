package com.gresk.modules.account.application.usecase;

import com.gresk.modules.account.domain.exception.AccountNotFoundException;
import com.gresk.modules.account.domain.model.Account;
import com.gresk.modules.account.domain.model.AccountId;
import com.gresk.modules.account.domain.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuspendAccountUseCase {
    private final AccountRepositoryPort accountRepository;

    @Transactional
    public void execute(UUID promoterId) {

        Account account = accountRepository.findById(AccountId.of(promoterId))
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + promoterId));

        account.suspend();
        accountRepository.save(account);
    }
}
