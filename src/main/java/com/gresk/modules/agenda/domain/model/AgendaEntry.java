package com.gresk.modules.agenda.domain.model;

import com.gresk.modules.agenda.domain.exception.InvalidAgendaEntryException;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;

public final class AgendaEntry {

    private final AgendaEntryId id;
    private final PromoterId    promoterId;
    private final Instant       createdAt;

    private EntryType       type;
    private String          title;
    private String          description;
    private Instant         startAt;
    private Instant         endAt;
    private boolean         allDay;
    private boolean         completed;
    private boolean         cancelled;
    private String          color;
    private String          label;
    private EntityReference linkedEntity;

    // ── Recurrencia ──────────────────────────────────────────────────────────
    private RecurrenceRule  recurrenceRule;   // null → entrada simple
    private AgendaEntryId   seriesId;         // null → maestra o simple; no-null → excepción
    private Instant         exceptionDate;    // fecha de la ocurrencia original que reemplaza

    // ── Recordatorio ─────────────────────────────────────────────────────────
    private Integer reminderMinutesBefore;
    private boolean reminderSent;

    private AgendaEntry(AgendaEntryId id, PromoterId promoterId, Instant createdAt,
                        EntryType type, String title, String description,
                        Instant startAt, Instant endAt, boolean allDay,
                        boolean completed, boolean cancelled,
                        String color, String label, EntityReference linkedEntity,
                        RecurrenceRule recurrenceRule, AgendaEntryId seriesId, Instant exceptionDate,
                        Integer reminderMinutesBefore, boolean reminderSent) {
        this.id                     = id;
        this.promoterId             = promoterId;
        this.createdAt              = createdAt;
        this.type                   = type;
        this.title                  = title;
        this.description            = description;
        this.startAt                = startAt;
        this.endAt                  = endAt;
        this.allDay                 = allDay;
        this.completed              = completed;
        this.cancelled              = cancelled;
        this.color                  = color;
        this.label                  = label;
        this.linkedEntity           = linkedEntity;
        this.recurrenceRule         = recurrenceRule;
        this.seriesId               = seriesId;
        this.exceptionDate          = exceptionDate;
        this.reminderMinutesBefore  = reminderMinutesBefore;
        this.reminderSent           = reminderSent;
    }

    // ── Factoría de creación ─────────────────────────────────────────────────

    public static AgendaEntry create(EntryType type, String title, PromoterId promoterId,
                                     String description, Instant startAt, Instant endAt,
                                     boolean allDay, String color, String label,
                                     EntityReference linkedEntity,
                                     RecurrenceRule recurrenceRule,
                                     Integer reminderMinutesBefore) {
        validate(type, title, startAt, endAt);
        return new AgendaEntry(
                AgendaEntryId.generate(), promoterId, Instant.now(),
                type, title, description, startAt, endAt, allDay,
                false, false, color, label, linkedEntity,
                recurrenceRule, null, null,
                reminderMinutesBefore, false
        );
    }

    /** Reconstitución completa desde persistencia. */
    public static AgendaEntry reconstitute(AgendaEntryId id, PromoterId promoterId, Instant createdAt,
                                           EntryType type, String title, String description,
                                           Instant startAt, Instant endAt, boolean allDay,
                                           boolean completed, boolean cancelled,
                                           String color, String label, EntityReference linkedEntity,
                                           RecurrenceRule recurrenceRule,
                                           AgendaEntryId seriesId, Instant exceptionDate,
                                           Integer reminderMinutesBefore, boolean reminderSent) {
        return new AgendaEntry(id, promoterId, createdAt,
                type, title, description, startAt, endAt, allDay,
                completed, cancelled, color, label, linkedEntity,
                recurrenceRule, seriesId, exceptionDate,
                reminderMinutesBefore, reminderSent);
    }

    // ── Comportamientos ──────────────────────────────────────────────────────

    /** Actualiza los campos editables de la entrada. */
    public void update(String title, String description, Instant startAt, Instant endAt,
                       boolean allDay, String color, String label,
                       EntityReference linkedEntity, Integer reminderMinutesBefore) {
        validate(this.type, title, startAt, endAt);
        this.title                  = title;
        this.description            = description;
        this.startAt                = startAt;
        this.endAt                  = endAt;
        this.allDay                 = allDay;
        this.color                  = color;
        this.label                  = label;
        this.linkedEntity           = linkedEntity;
        this.reminderMinutesBefore  = reminderMinutesBefore;
        this.reminderSent           = false; // reset para que se vuelva a enviar
    }

    /** Marca la tarea como completada. Solo aplica a {@link EntryType#TASK}. */
    public void complete() {
        if (type != EntryType.TASK) {
            throw new InvalidAgendaEntryException("Only TASK entries can be completed");
        }
        if (completed) {
            throw new InvalidAgendaEntryException("Task is already completed");
        }
        this.completed = true;
    }

    /** Reabre una tarea completada. */
    public void reopen() {
        if (type != EntryType.TASK) {
            throw new InvalidAgendaEntryException("Only TASK entries can be reopened");
        }
        this.completed = false;
    }

    /** Marca la entrada como cancelada (solo para excepciones de serie). */
    public void cancel() {
        this.cancelled = true;
    }

    /** Marca el recordatorio como enviado. */
    public void markReminderSent() {
        this.reminderSent = true;
    }

    /** Asocia esta entrada como excepción de una serie. */
    public void attachToSeries(AgendaEntryId seriesId, Instant exceptionDate) {
        this.seriesId      = seriesId;
        this.exceptionDate = exceptionDate;
    }

    /** Acorta la serie hasta la fecha dada (exclusive), usado en THIS_AND_FOLLOWING. */
    public void truncateSeriesUntil(Instant newUntil) {
        if (recurrenceRule == null) {
            throw new InvalidAgendaEntryException("Entry has no recurrence rule to truncate");
        }
        this.recurrenceRule = new RecurrenceRule(
                recurrenceRule.frequency(),
                recurrenceRule.interval(),
                null,           // count se descarta al establecer until
                newUntil,
                recurrenceRule.byDay(),
                recurrenceRule.byMonthDay()
        );
    }

    // ── Validaciones de dominio ──────────────────────────────────────────────

    private static void validate(EntryType type, String title, Instant startAt, Instant endAt) {
        if (title == null || title.isBlank()) {
            throw new InvalidAgendaEntryException("Agenda entry title must not be blank");
        }
        if (type == EntryType.APPOINTMENT || type == EntryType.REMINDER) {
            if (startAt == null) {
                throw new InvalidAgendaEntryException(type + " requires a startAt date");
            }
        }
        if (type == EntryType.APPOINTMENT && endAt != null && startAt != null && endAt.isBefore(startAt)) {
            throw new InvalidAgendaEntryException("APPOINTMENT endAt must be after startAt");
        }
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public AgendaEntryId   getId()                    { return id; }
    public PromoterId      getPromoterId()            { return promoterId; }
    public Instant         getCreatedAt()             { return createdAt; }
    public EntryType       getType()                  { return type; }
    public String          getTitle()                 { return title; }
    public String          getDescription()           { return description; }
    public Instant         getStartAt()               { return startAt; }
    public Instant         getEndAt()                 { return endAt; }
    public boolean         isAllDay()                 { return allDay; }
    public boolean         isCompleted()              { return completed; }
    public boolean         isCancelled()              { return cancelled; }
    public String          getColor()                 { return color; }
    public String          getLabel()                 { return label; }
    public EntityReference getLinkedEntity()          { return linkedEntity; }
    public RecurrenceRule  getRecurrenceRule()        { return recurrenceRule; }
    public AgendaEntryId   getSeriesId()              { return seriesId; }
    public Instant         getExceptionDate()         { return exceptionDate; }
    public Integer         getReminderMinutesBefore() { return reminderMinutesBefore; }
    public boolean         isReminderSent()           { return reminderSent; }

    public boolean isRecurring()  { return recurrenceRule != null; }
    public boolean isException()  { return seriesId != null; }
}
