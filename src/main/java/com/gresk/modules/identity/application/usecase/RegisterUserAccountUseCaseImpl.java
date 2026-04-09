package com.gresk.modules.identity.application.usecase;

import com.gresk.modules.identity.application.command.RegisterUserAccountCommand;
import com.gresk.modules.identity.application.port.in.RegisterUserAccountUseCase;
import com.gresk.modules.identity.application.port.out.PasswordHasherPort;
import com.gresk.modules.identity.domain.exception.AccountAlreadyExistsException;
import com.gresk.modules.identity.domain.model.Account;
import com.gresk.modules.identity.domain.model.AccountId;
import com.gresk.modules.identity.domain.port.out.AccountRepositoryPort;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.event.UserRegisteredEvent;
import com.gresk.shared.domain.port.out.ImageStoragePort;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterUserAccountUseCaseImpl implements RegisterUserAccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;
    private final ImageStoragePort imageStorage;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public AccountId execute(RegisterUserAccountCommand command) {
        Email email = Email.of(command.email());

        if (accountRepositoryPort.existsByEmail(email)) {
            throw new AccountAlreadyExistsException(command.email());
        }

        String passwordHash = passwordHasherPort.hash(command.rawPassword());

        Account account = Account.create(email, passwordHash, command.roles(), AccountStatus.ACTIVE);

        String avatarAssetId = null;
        if (command.avatar() != null && !command.avatar().isEmpty()) {
            AssetId assetId = imageStorage.upload(command.avatar(), "users/avatars");
            avatarAssetId = assetId.value();
        }

        eventPublisher.publishEvent(new UserRegisteredEvent(
                account.getId().value(),
                account.getEmail().value(),
                command.name(),
                command.description(),
                command.city(),
                command.musicGenres(),
                avatarAssetId
        ));

        return accountRepositoryPort.save(account);
    }
}
