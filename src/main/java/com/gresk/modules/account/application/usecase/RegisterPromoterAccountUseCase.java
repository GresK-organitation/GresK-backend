package com.gresk.modules.account.application.usecase;

import com.gresk.modules.account.application.command.RegisterPromoterAccountCommand;
import com.gresk.modules.account.domain.port.out.PasswordHasherPort;
import com.gresk.modules.account.domain.exception.AccountAlreadyExistsException;
import com.gresk.modules.account.domain.model.Account;
import com.gresk.modules.account.domain.model.AccountId;
import com.gresk.modules.account.domain.port.out.AccountRepositoryPort;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.modules.account.infrastructure.event.PromoterRegisteredEvent;
import com.gresk.shared.domain.Role;
import com.gresk.shared.domain.port.out.ImageStoragePort;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegisterPromoterAccountUseCase {

    private final AccountRepositoryPort accountRepository;
    private final PasswordHasherPort passwordHasher;
    private final ImageStoragePort imageStorage;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AccountId execute(RegisterPromoterAccountCommand command) {
        Email email = Email.of(command.email());

        if (accountRepository.existsByEmail(email)) {
            throw new AccountAlreadyExistsException(command.email());
        }

        String passwordHash = passwordHasher.hash(command.rawPassword());
        Account account = Account.create(email, passwordHash, Set.of(Role.PROMOTER) , AccountStatus.PENDING);

        String logoAssetId = null;
        if (command.logo() != null && !command.logo().isEmpty()) {
            AssetId assetId = imageStorage.upload(command.logo(), "promoters/logos");
            logoAssetId = assetId.value();
        }

        AccountId accountId = accountRepository.save(account).getId();

        eventPublisher.publishEvent(new PromoterRegisteredEvent(
                accountId.value(),
                account.getEmail().value(),
                command.companyName(),
                command.description(),
                command.street(),
                command.city(),
                command.country(),
                command.musicalGenres(),
                logoAssetId,
                command.phone(),
                command.website()
        ));

        return accountId;
    }
}
