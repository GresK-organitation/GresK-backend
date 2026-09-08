package com.gresk.modules.agenda.application.usecase;

import com.gresk.modules.agenda.application.command.SyncCalendarCommand;
import com.gresk.modules.agenda.application.dto.SyncResult;
import com.gresk.modules.agenda.application.port.in.SyncCalendarUseCase;
import com.gresk.modules.agenda.domain.exception.CalendarAccountNotFoundException;
import com.gresk.modules.agenda.domain.exception.CalendarSyncNotConnectedException;
import com.gresk.modules.agenda.domain.model.AgendaEntry;
import com.gresk.modules.agenda.domain.model.AgendaEntryId;
import com.gresk.modules.agenda.domain.model.CalendarEventMapping;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccount;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccountId;
import com.gresk.modules.agenda.domain.model.EntryType;
import com.gresk.modules.agenda.domain.model.LocalEntryType;
import com.gresk.modules.agenda.domain.model.OAuthTokenRef;
import com.gresk.modules.agenda.domain.model.SyncStatus;
import com.gresk.modules.agenda.domain.port.out.AgendaEntryRepository;
import com.gresk.modules.agenda.domain.port.out.CalendarEventSyncPort;
import com.gresk.modules.agenda.domain.port.out.CalendarEventSyncPort.ExternalCalendarChanges;
import com.gresk.modules.agenda.domain.port.out.CalendarEventSyncPort.ExternalCalendarEvent;
import com.gresk.modules.agenda.domain.port.out.CalendarEventSyncPort.ExternalCalendarEventDraft;
import com.gresk.modules.agenda.domain.port.out.CalendarSyncAccountRepository;
import com.gresk.modules.agenda.domain.port.out.OAuthCalendarClientPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Sincronización bidireccional Gresk ↔ calendario externo para {@link AgendaEntry} (entradas
 * simples, sin recurrencia — la expansión de series recurrentes contra un calendario externo
 * queda fuera de esta primera versión). Solo cubre AGENDA_ENTRY: el sync de {@code Booking}
 * es push-only y se añadirá cuando exista el puerto anti-corrupción hacia el módulo booking.
 * <p>
 * Limitación conocida: {@code AgendaEntry} no registra {@code updatedAt}, así que la detección
 * de conflictos real (edición local + externa simultánea) no es posible en esta versión —
 * {@code conflictCount} se reporta siempre en 0 en lugar de simularse.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SyncCalendarService implements SyncCalendarUseCase {

    private static final int PUSH_WINDOW_DAYS = 90;

    private final CalendarSyncAccountRepository accountRepository;
    private final AgendaEntryRepository agendaEntryRepository;
    private final CalendarProviderAdapterResolver adapterResolver;

    @Override
    public SyncResult execute(SyncCalendarCommand command) {
        CalendarSyncAccountId id = CalendarSyncAccountId.of(command.calendarSyncAccountId());
        CalendarSyncAccount account = accountRepository.findById(id)
                .orElseThrow(() -> new CalendarAccountNotFoundException(command.calendarSyncAccountId()));
        if (!account.getPromoterId().equals(PromoterId.of(command.promoterId()))) {
            throw new CalendarAccountNotFoundException(command.calendarSyncAccountId());
        }
        if (account.getStatus() != SyncStatus.CONNECTED) {
            throw new CalendarSyncNotConnectedException(command.calendarSyncAccountId());
        }

        OAuthCalendarClientPort oAuthClient = adapterResolver.oAuthClientFor(account.getProvider());
        CalendarEventSyncPort eventSyncClient = adapterResolver.eventSyncClientFor(account.getProvider());

        OAuthTokenRef tokenRef = oAuthClient.refreshIfNeeded(account.getTokenRef());
        if (!tokenRef.equals(account.getTokenRef())) {
            account.updateTokenRef(tokenRef);
        }

        try {
            int pulled = pull(account, tokenRef, eventSyncClient);
            int pushed = push(account, tokenRef, eventSyncClient);
            Instant now = Instant.now();
            account.recordSuccessfulSync(account.getSyncToken(), now);
            accountRepository.save(account);
            return new SyncResult(pushed, pulled, 0, now);
        } catch (Exception e) {
            log.error("Calendar sync failed for account {}: {}", id, e.getMessage());
            account.recordSyncError(e.getMessage());
            accountRepository.save(account);
            throw new IllegalStateException("Calendar sync failed", e);
        }
    }

    private int pull(CalendarSyncAccount account, OAuthTokenRef tokenRef, CalendarEventSyncPort eventSyncClient) {
        ExternalCalendarChanges changes = eventSyncClient.pullChanges(tokenRef, account.getSyncToken());
        int count = 0;

        for (ExternalCalendarEvent external : changes.upserts()) {
            var mapping = account.findMappingByExternalId(external.externalEventId());
            if (mapping.isPresent() && mapping.get().localType() == LocalEntryType.AGENDA_ENTRY) {
                agendaEntryRepository.findById(AgendaEntryId.of(mapping.get().localEntryId())).ifPresent(entry -> {
                    entry.update(external.title(), entry.getDescription(), external.startAt(), external.endAt(),
                            entry.isAllDay(), entry.getColor(), entry.getLabel(), entry.getLinkedEntity(),
                            entry.getReminderMinutesBefore());
                    agendaEntryRepository.save(entry);
                });
                account.upsertMapping(new CalendarEventMapping(mapping.get().localEntryId(), LocalEntryType.AGENDA_ENTRY,
                        external.externalEventId(), external.etag(), mapping.get().lastPushedAt(), Instant.now()));
            } else if (mapping.isEmpty()) {
                AgendaEntry created = AgendaEntry.create(EntryType.APPOINTMENT, external.title(), account.getPromoterId(),
                        null, external.startAt(), external.endAt(), false, null, null, null, null, null);
                AgendaEntry saved = agendaEntryRepository.save(created);
                account.upsertMapping(new CalendarEventMapping(saved.getId().value(), LocalEntryType.AGENDA_ENTRY,
                        external.externalEventId(), external.etag(), null, Instant.now()));
            }
            count++;
        }

        for (String deletedExternalId : changes.deletedExternalIds()) {
            account.findMappingByExternalId(deletedExternalId).ifPresent(mapping -> {
                if (mapping.localType() == LocalEntryType.AGENDA_ENTRY) {
                    agendaEntryRepository.deleteById(AgendaEntryId.of(mapping.localEntryId()));
                }
                account.removeMapping(mapping);
            });
        }

        account.recordSuccessfulSync(changes.newSyncToken(), Instant.now());
        return count;
    }

    private int push(CalendarSyncAccount account, OAuthTokenRef tokenRef, CalendarEventSyncPort eventSyncClient) {
        Instant now = Instant.now();
        Instant windowEnd = now.plus(PUSH_WINDOW_DAYS, ChronoUnit.DAYS);
        int count = 0;
        for (AgendaEntry entry : agendaEntryRepository.findSimpleByPromoterAndDateRange(account.getPromoterId(), now, windowEnd)) {
            if (account.findMapping(entry.getId().value(), LocalEntryType.AGENDA_ENTRY).isPresent()) {
                continue;
            }
            ExternalCalendarEventDraft draft = new ExternalCalendarEventDraft(entry.getTitle(), entry.getStartAt(), entry.getEndAt());
            String externalId = eventSyncClient.pushEvent(tokenRef, draft, null);
            account.upsertMapping(new CalendarEventMapping(entry.getId().value(), LocalEntryType.AGENDA_ENTRY,
                    externalId, null, now, null));
            count++;
        }
        return count;
    }
}
