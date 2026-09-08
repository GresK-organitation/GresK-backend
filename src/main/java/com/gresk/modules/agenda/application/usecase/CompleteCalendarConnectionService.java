package com.gresk.modules.agenda.application.usecase;

import com.gresk.modules.agenda.application.command.CompleteCalendarConnectionCommand;
import com.gresk.modules.agenda.application.port.in.CompleteCalendarConnectionUseCase;
import com.gresk.modules.agenda.domain.exception.CalendarAccountAlreadyConnectedException;
import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccount;
import com.gresk.modules.agenda.domain.model.SyncStatus;
import com.gresk.modules.agenda.domain.port.out.CalendarSyncAccountRepository;
import com.gresk.modules.agenda.domain.port.out.OAuthCalendarClientPort;
import com.gresk.modules.agenda.domain.port.out.OAuthCalendarClientPort.ExternalAccountAuthResult;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class CompleteCalendarConnectionService implements CompleteCalendarConnectionUseCase {

    private final CalendarOAuthStateStore stateStore;
    private final CalendarProviderAdapterResolver adapterResolver;
    private final CalendarSyncAccountRepository accountRepository;
    private final SecureRandom random = new SecureRandom();

    @Override
    public CalendarSyncAccount execute(CompleteCalendarConnectionCommand command) {
        CalendarOAuthStateStore.PendingState pending = stateStore.consume(command.state());
        CalendarProvider provider = CalendarProvider.valueOf(command.provider());
        PromoterId promoterId = pending.promoterId();

        OAuthCalendarClientPort oAuthClient = adapterResolver.oAuthClientFor(provider);
        ExternalAccountAuthResult result = oAuthClient.exchangeAuthorizationCode(command.code());

        var existing = accountRepository.findByPromoterAndProvider(promoterId, provider);
        if (existing.isPresent() && existing.get().getStatus() == SyncStatus.CONNECTED) {
            throw new CalendarAccountAlreadyConnectedException(promoterId.toString(), provider.name());
        }

        CalendarSyncAccount account = CalendarSyncAccount.connect(promoterId, provider,
                result.externalAccountEmail(), result.tokenRef(), generateClientStateSecret());
        return accountRepository.save(account);
    }

    private String generateClientStateSecret() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
