package com.gresk.modules.agenda.application.usecase;

import com.gresk.modules.agenda.application.port.in.GetCalendarAuthorizationUrlUseCase;
import com.gresk.modules.agenda.application.query.GetCalendarAuthorizationUrlQuery;
import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCalendarAuthorizationUrlService implements GetCalendarAuthorizationUrlUseCase {

    private final CalendarOAuthStateStore stateStore;
    private final CalendarProviderAdapterResolver adapterResolver;

    @Override
    public String execute(GetCalendarAuthorizationUrlQuery query) {
        CalendarProvider provider = CalendarProvider.valueOf(query.provider());
        PromoterId promoterId = PromoterId.of(query.promoterId());
        String state = stateStore.issue(promoterId, provider);
        return adapterResolver.oAuthClientFor(provider).buildAuthorizationUrl(state);
    }
}
