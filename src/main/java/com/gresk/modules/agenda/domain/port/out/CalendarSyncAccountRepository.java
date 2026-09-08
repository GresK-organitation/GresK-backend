package com.gresk.modules.agenda.domain.port.out;

import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccount;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccountId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface CalendarSyncAccountRepository {

    CalendarSyncAccount save(CalendarSyncAccount account);

    Optional<CalendarSyncAccount> findById(CalendarSyncAccountId id);

    Optional<CalendarSyncAccount> findByPromoterAndProvider(PromoterId promoterId, CalendarProvider provider);

    List<CalendarSyncAccount> findByPromoter(PromoterId promoterId);

    List<CalendarSyncAccount> findAllConnected();

    Optional<CalendarSyncAccount> findByGoogleWatchChannelId(String channelId);

    Optional<CalendarSyncAccount> findByOutlookSubscriptionId(String subscriptionId);

    void deleteById(CalendarSyncAccountId id);
}
