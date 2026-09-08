package com.gresk.modules.agenda.infrastructure.scheduler;

import com.gresk.modules.agenda.application.command.SyncCalendarCommand;
import com.gresk.modules.agenda.application.port.in.SyncCalendarUseCase;
import com.gresk.modules.agenda.application.usecase.CalendarProviderAdapterResolver;
import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccount;
import com.gresk.modules.agenda.domain.model.OAuthTokenRef;
import com.gresk.modules.agenda.domain.port.out.CalendarSyncAccountRepository;
import com.gresk.modules.agenda.infrastructure.google.GoogleCalendarProperties;
import com.gresk.modules.agenda.infrastructure.outlook.OutlookProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Fallback de polling (para cuentas sin webhook configurado o cuando este falla) y
 * renovación proactiva de los canales/suscripciones push antes de que expiren —
 * mismo patrón {@code @Scheduled} que {@code ReminderEmailScheduler} en agenda:
 * toda la lógica de negocio vive en los casos de uso, el scheduler solo orquesta.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CalendarSyncScheduler {

    private final CalendarSyncAccountRepository accountRepository;
    private final SyncCalendarUseCase syncCalendarUseCase;
    private final CalendarProviderAdapterResolver adapterResolver;
    private final GoogleCalendarProperties googleProperties;
    private final OutlookProperties outlookProperties;

    @Scheduled(fixedDelay = 900_000) // 15 minutos
    public void pollConnectedAccounts() {
        for (CalendarSyncAccount account : accountRepository.findAllConnected()) {
            try {
                syncCalendarUseCase.execute(new SyncCalendarCommand(account.getId().toString(), account.getPromoterId().toString()));
            } catch (Exception e) {
                log.warn("Fallback sync failed for calendar account {}: {}", account.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    @Scheduled(fixedDelay = 3_600_000) // cada hora
    public void renewExpiringWatches() {
        Instant renewalThreshold = Instant.now().plusSeconds(6 * 3600);
        for (CalendarSyncAccount account : accountRepository.findAllConnected()) {
            try {
                if (account.getProvider() == CalendarProvider.GOOGLE) {
                    renewGoogleWatchIfNeeded(account, renewalThreshold);
                } else {
                    renewOutlookSubscriptionIfNeeded(account, renewalThreshold);
                }
            } catch (Exception e) {
                log.warn("Watch renewal failed for calendar account {}: {}", account.getId(), e.getMessage());
            }
        }
    }

    private void renewGoogleWatchIfNeeded(CalendarSyncAccount account, Instant renewalThreshold) {
        if (googleProperties.webhookUrl().isBlank()) return;
        if (account.getWatchExpiry() != null && account.getWatchExpiry().isAfter(renewalThreshold)) return;

        OAuthTokenRef tokenRef = adapterResolver.oAuthClientFor(account.getProvider()).refreshIfNeeded(account.getTokenRef());
        var registration = adapterResolver.eventSyncClientFor(account.getProvider())
                .registerWatch(tokenRef, googleProperties.webhookUrl(), account.getClientStateSecret());
        account.updateTokenRef(tokenRef);
        account.registerGoogleWatch(registration.channelOrSubscriptionId(), registration.resourceId(), registration.expiry());
        accountRepository.save(account);
    }

    private void renewOutlookSubscriptionIfNeeded(CalendarSyncAccount account, Instant renewalThreshold) {
        if (outlookProperties.webhookUrl().isBlank()) return;
        if (account.getMsSubscriptionExpiry() != null && account.getMsSubscriptionExpiry().isAfter(renewalThreshold)) return;

        OAuthTokenRef tokenRef = adapterResolver.oAuthClientFor(account.getProvider()).refreshIfNeeded(account.getTokenRef());
        var registration = adapterResolver.eventSyncClientFor(account.getProvider())
                .registerWatch(tokenRef, outlookProperties.webhookUrl(), account.getClientStateSecret());
        account.updateTokenRef(tokenRef);
        account.registerOutlookSubscription(registration.channelOrSubscriptionId(), registration.expiry());
        accountRepository.save(account);
    }
}
