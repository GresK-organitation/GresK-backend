package com.gresk.modules.agenda.application.usecase;

import com.gresk.modules.agenda.application.command.HandleCalendarWebhookCommand;
import com.gresk.modules.agenda.application.command.SyncCalendarCommand;
import com.gresk.modules.agenda.application.port.in.HandleCalendarWebhookUseCase;
import com.gresk.modules.agenda.application.port.in.SyncCalendarUseCase;
import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccount;
import com.gresk.modules.agenda.domain.port.out.CalendarSyncAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Procesa una notificación push de Google Calendar (canal watch) o Microsoft Graph
 * (suscripción) — resuelve la cuenta por el id de canal/suscripción (no por email,
 * a diferencia del webhook de Gmail), valida el {@code clientState}/{@code channelToken}
 * propio de la cuenta, y dispara un sync incremental de forma asíncrona.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HandleCalendarWebhookService implements HandleCalendarWebhookUseCase {

    private final CalendarSyncAccountRepository accountRepository;
    private final SyncCalendarUseCase syncCalendarUseCase;

    @Override
    @Async
    public void execute(HandleCalendarWebhookCommand command) {
        CalendarProvider provider = CalendarProvider.valueOf(command.provider());
        Optional<CalendarSyncAccount> account = provider == CalendarProvider.GOOGLE
                ? accountRepository.findByGoogleWatchChannelId(command.channelOrSubscriptionId())
                : accountRepository.findByOutlookSubscriptionId(command.channelOrSubscriptionId());

        if (account.isEmpty()) {
            log.warn("Calendar webhook received for unknown channel/subscription {} ({})",
                    command.channelOrSubscriptionId(), provider);
            return;
        }
        if (!account.get().getClientStateSecret().equals(command.clientStateToken())) {
            log.warn("Calendar webhook clientState mismatch for account {}", account.get().getId());
            return;
        }

        syncCalendarUseCase.execute(new SyncCalendarCommand(account.get().getId().toString(),
                account.get().getPromoterId().toString()));
    }
}
