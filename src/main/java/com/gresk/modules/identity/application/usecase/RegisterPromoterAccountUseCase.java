package com.gresk.modules.identity.application.usecase;

import com.gresk.modules.identity.application.command.RegisterPromoterAccountCommand;
import com.gresk.modules.identity.application.port.out.PasswordHasherPort;
import com.gresk.modules.identity.domain.exception.AccountAlreadyExistsException;
import com.gresk.modules.identity.domain.model.Account;
import com.gresk.modules.identity.domain.model.AccountId;
import com.gresk.modules.identity.domain.port.out.AccountRepositoryPort;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.event.PromoterRegisteredEvent;
import com.gresk.shared.domain.port.out.ImageStoragePort;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Account account = Account.create(email, passwordHash, command.roles(), AccountStatus.PENDING);

        String logoAssetId = null;
        if (command.logo() != null && !command.logo().isEmpty()) {
            AssetId assetId = imageStorage.upload(command.logo(), "promoters/logos");
            logoAssetId = assetId.value();
        }

        eventPublisher.publishEvent(new PromoterRegisteredEvent(
                account.getId().value(),
                account.getEmail().value(),
                command.companyName(),
                command.description(),
                command.address(),
                command.city(),
                command.country(),
                command.musicalGenres(),
                logoAssetId
        ));

        return accountRepository.save(account);
    }
}
