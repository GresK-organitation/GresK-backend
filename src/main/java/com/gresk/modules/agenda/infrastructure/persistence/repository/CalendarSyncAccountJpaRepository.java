package com.gresk.modules.agenda.infrastructure.persistence.repository;

import com.gresk.modules.agenda.infrastructure.persistence.entity.CalendarSyncAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CalendarSyncAccountJpaRepository extends JpaRepository<CalendarSyncAccountEntity, UUID> {

    Optional<CalendarSyncAccountEntity> findByPromoterIdAndProvider(UUID promoterId, String provider);

    List<CalendarSyncAccountEntity> findByPromoterId(UUID promoterId);

    List<CalendarSyncAccountEntity> findByStatus(String status);

    Optional<CalendarSyncAccountEntity> findByWatchChannelId(String watchChannelId);

    Optional<CalendarSyncAccountEntity> findByMsSubscriptionId(String msSubscriptionId);
}
