package com.gresk.modules.agenda.infrastructure.persistence;

import com.gresk.modules.agenda.domain.model.EntryType;
import com.gresk.modules.agenda.domain.model.LinkedEntityType;
import com.gresk.modules.agenda.domain.model.RecurrenceFrequency;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "agenda_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaEntryEntity {

    @Id
    private UUID id;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EntryType type;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ── Temporalidad ──────────────────────────────────────────────────────────
    @Column(name = "start_at")
    private Instant startAt;

    @Column(name = "end_at")
    private Instant endAt;

    @Column(name = "all_day", nullable = false)
    private boolean allDay;

    // ── Estado ────────────────────────────────────────────────────────────────
    @Column(nullable = false)
    private boolean completed;

    @Column(name = "is_cancelled", nullable = false)
    private boolean cancelled;

    // ── Personalización visual ────────────────────────────────────────────────
    @Column(length = 7)
    private String color;

    @Column(length = 100)
    private String label;

    // ── Vínculo opcional ──────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "linked_entity_type", length = 20)
    private LinkedEntityType linkedEntityType;

    @Column(name = "linked_entity_id")
    private UUID linkedEntityId;

    // ── Recurrencia ────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_frequency", length = 10)
    private RecurrenceFrequency recurrenceFrequency;

    @Column(name = "recurrence_interval")
    private Integer recurrenceInterval;

    @Column(name = "recurrence_count")
    private Integer recurrenceCount;

    @Column(name = "recurrence_until")
    private Instant recurrenceUntil;

    @Column(name = "recurrence_by_day", length = 50)
    private String recurrenceByDay;

    @Column(name = "recurrence_by_month_day")
    private Integer recurrenceByMonthDay;

    // ── Serie / Excepciones ───────────────────────────────────────────────────
    @Column(name = "series_id")
    private UUID seriesId;

    @Column(name = "exception_date")
    private Instant exceptionDate;

    // ── Recordatorio ──────────────────────────────────────────────────────────
    @Column(name = "reminder_minutes_before")
    private Integer reminderMinutesBefore;

    @Column(name = "reminder_sent", nullable = false)
    private boolean reminderSent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
