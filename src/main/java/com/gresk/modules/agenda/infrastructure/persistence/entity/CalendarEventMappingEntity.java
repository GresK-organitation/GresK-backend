package com.gresk.modules.agenda.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "calendar_event_mappings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventMappingEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sync_account_id", nullable = false)
    private CalendarSyncAccountEntity account;

    @Column(name = "local_entry_id", nullable = false)
    private UUID localEntryId;

    @Column(name = "local_type", nullable = false)
    private String localType;

    @Column(name = "external_event_id", nullable = false)
    private String externalEventId;

    @Column(name = "external_etag")
    private String externalEtag;

    @Column(name = "last_pushed_at")
    private Instant lastPushedAt;

    @Column(name = "last_pulled_at")
    private Instant lastPulledAt;
}
