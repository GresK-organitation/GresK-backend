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
@Transactional
@RequiredArgsConstructor
public class ApprovePromoterUseCase {
    private final AccountRepositoryPort accountRepository;

    @Transactional
    public void execute(UUID accountId) {
        Account account = accountRepository.findById(AccountId.of(accountId))
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        account.approvePromoter();
        accountRepository.save(account);
    }
}
