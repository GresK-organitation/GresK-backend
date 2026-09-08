package com.gresk.modules.agenda.infrastructure.persistence.mapper;

import com.gresk.modules.agenda.domain.model.CalendarEventMapping;
import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccount;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccountId;
import com.gresk.modules.agenda.domain.model.LocalEntryType;
import com.gresk.modules.agenda.domain.model.OAuthTokenRef;
import com.gresk.modules.agenda.domain.model.SyncStatus;
import com.gresk.modules.agenda.infrastructure.persistence.entity.CalendarEventMappingEntity;
import com.gresk.modules.agenda.infrastructure.persistence.entity.CalendarSyncAccountEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CalendarSyncAccountMapper {

    public CalendarSyncAccount toDomain(CalendarSyncAccountEntity entity) {
        OAuthTokenRef tokenRef = entity.getAccessTokenCiphertext() == null ? null
                : new OAuthTokenRef(entity.getAccessTokenCiphertext(), entity.getRefreshTokenCiphertext(), entity.getTokenExpiry());

        List<CalendarEventMapping> mappings = entity.getMappings().stream()
                .map(m -> new CalendarEventMapping(m.getLocalEntryId(), LocalEntryType.valueOf(m.getLocalType()),
                        m.getExternalEventId(), m.getExternalEtag(), m.getLastPushedAt(), m.getLastPulledAt()))
                .toList();

        return CalendarSyncAccount.reconstitute(
                CalendarSyncAccountId.of(entity.getId()),
                PromoterId.of(entity.getPromoterId()),
                CalendarProvider.valueOf(entity.getProvider()),
                entity.getCreatedAt(),
                entity.getExternalAccountEmail(),
                tokenRef,
                SyncStatus.valueOf(entity.getStatus()),
                entity.getSyncToken(),
                entity.getLastSyncedAt(),
                entity.getLastError(),
                entity.getWatchChannelId(),
                entity.getWatchResourceId(),
                entity.getWatchExpiry(),
                entity.getMsSubscriptionId(),
                entity.getMsSubscriptionExpiry(),
                entity.getClientStateSecret(),
                mappings,
                entity.getUpdatedAt()
        );
    }

    public CalendarSyncAccountEntity toEntity(CalendarSyncAccount account) {
        OAuthTokenRef tokenRef = account.getTokenRef();

        CalendarSyncAccountEntity entity = CalendarSyncAccountEntity.builder()
                .id(account.getId().value())
                .promoterId(account.getPromoterId().value())
                .provider(account.getProvider().name())
                .externalAccountEmail(account.getExternalAccountEmail())
                .accessTokenCiphertext(tokenRef == null ? null : tokenRef.accessTokenCiphertext())
                .refreshTokenCiphertext(tokenRef == null ? null : tokenRef.refreshTokenCiphertext())
                .tokenExpiry(tokenRef == null ? null : tokenRef.tokenExpiry())
                .status(account.getStatus().name())
                .syncToken(account.getSyncToken())
                .lastSyncedAt(account.getLastSyncedAt())
                .lastError(account.getLastError())
                .watchChannelId(account.getWatchChannelId())
                .watchResourceId(account.getWatchResourceId())
                .watchExpiry(account.getWatchExpiry())
                .msSubscriptionId(account.getMsSubscriptionId())
                .msSubscriptionExpiry(account.getMsSubscriptionExpiry())
                .clientStateSecret(account.getClientStateSecret())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();

        List<CalendarEventMappingEntity> mappingEntities = new ArrayList<>();
        for (CalendarEventMapping m : account.getMappings()) {
            mappingEntities.add(CalendarEventMappingEntity.builder()
                    .id(UUID.randomUUID())
                    .account(entity)
                    .localEntryId(m.localEntryId())
                    .localType(m.localType().name())
                    .externalEventId(m.externalEventId())
                    .externalEtag(m.externalEtag())
                    .lastPushedAt(m.lastPushedAt())
                    .lastPulledAt(m.lastPulledAt())
                    .build());
        }
        entity.setMappings(mappingEntities);

        return entity;
    }
}
