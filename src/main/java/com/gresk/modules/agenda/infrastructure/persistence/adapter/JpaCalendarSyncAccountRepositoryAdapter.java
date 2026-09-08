package com.gresk.modules.agenda.infrastructure.persistence.adapter;

import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccount;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccountId;
import com.gresk.modules.agenda.domain.model.SyncStatus;
import com.gresk.modules.agenda.domain.port.out.CalendarSyncAccountRepository;
import com.gresk.modules.agenda.infrastructure.persistence.mapper.CalendarSyncAccountMapper;
import com.gresk.modules.agenda.infrastructure.persistence.repository.CalendarSyncAccountJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaCalendarSyncAccountRepositoryAdapter implements CalendarSyncAccountRepository {

    private final CalendarSyncAccountJpaRepository jpaRepository;
    private final CalendarSyncAccountMapper mapper;

    @Override
    @Transactional
    public CalendarSyncAccount save(CalendarSyncAccount account) {
        var saved = jpaRepository.save(mapper.toEntity(account));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CalendarSyncAccount> findById(CalendarSyncAccountId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<CalendarSyncAccount> findByPromoterAndProvider(PromoterId promoterId, CalendarProvider provider) {
        return jpaRepository.findByPromoterIdAndProvider(promoterId.value(), provider.name()).map(mapper::toDomain);
    }

    @Override
    public List<CalendarSyncAccount> findByPromoter(PromoterId promoterId) {
        return jpaRepository.findByPromoterId(promoterId.value()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<CalendarSyncAccount> findAllConnected() {
        return jpaRepository.findByStatus(SyncStatus.CONNECTED.name()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<CalendarSyncAccount> findByGoogleWatchChannelId(String channelId) {
        return jpaRepository.findByWatchChannelId(channelId).map(mapper::toDomain);
    }

    @Override
    public Optional<CalendarSyncAccount> findByOutlookSubscriptionId(String subscriptionId) {
        return jpaRepository.findByMsSubscriptionId(subscriptionId).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(CalendarSyncAccountId id) {
        jpaRepository.deleteById(id.value());
    }
}
