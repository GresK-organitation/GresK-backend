package com.gresk.modules.agenda.application.usecase;

import com.gresk.modules.agenda.application.command.DisconnectCalendarCommand;
import com.gresk.modules.agenda.application.port.in.DisconnectCalendarUseCase;
import com.gresk.modules.agenda.domain.exception.CalendarAccountNotFoundException;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccount;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccountId;
import com.gresk.modules.agenda.domain.port.out.CalendarSyncAccountRepository;
import com.gresk.modules.agenda.domain.port.out.OAuthCalendarClientPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DisconnectCalendarService implements DisconnectCalendarUseCase {

    private final CalendarSyncAccountRepository accountRepository;
    private final CalendarProviderAdapterResolver adapterResolver;

    @Override
    public void execute(DisconnectCalendarCommand command) {
        CalendarSyncAccountId id = CalendarSyncAccountId.of(command.calendarSyncAccountId());
        CalendarSyncAccount account = accountRepository.findById(id)
                .orElseThrow(() -> new CalendarAccountNotFoundException(command.calendarSyncAccountId()));
        if (!account.getPromoterId().equals(PromoterId.of(command.promoterId()))) {
            throw new CalendarAccountNotFoundException(command.calendarSyncAccountId());
        }

        OAuthCalendarClientPort oAuthClient = adapterResolver.oAuthClientFor(account.getProvider());
        if (account.getTokenRef() != null) {
            oAuthClient.revoke(account.getTokenRef());
        }
        account.disconnect();
        accountRepository.save(account);
    }
}
