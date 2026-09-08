package com.gresk.modules.agenda.application.usecase;

import com.gresk.modules.agenda.application.port.in.ListCalendarAccountsUseCase;
import com.gresk.modules.agenda.application.query.ListCalendarAccountsQuery;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccount;
import com.gresk.modules.agenda.domain.port.out.CalendarSyncAccountRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListCalendarAccountsService implements ListCalendarAccountsUseCase {

    private final CalendarSyncAccountRepository accountRepository;

    @Override
    public List<CalendarSyncAccount> execute(ListCalendarAccountsQuery query) {
        return accountRepository.findByPromoter(PromoterId.of(query.promoterId()));
    }
}
