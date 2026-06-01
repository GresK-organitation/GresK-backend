package com.gresk.modules.rider.domain.model;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.rider.domain.exception.ChecklistEntryNotFoundException;
import com.gresk.modules.rider.domain.model.valueobject.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class EventRiderChecklist {

    private final ChecklistId id;
    private final EventId     eventId;
    private final RiderId     riderId;
    private final Instant     createdAt;

    private List<ChecklistEntry> items;
    private Instant              alertSentAt;
    private Instant              updatedAt;

    private EventRiderChecklist(ChecklistId id, EventId eventId, RiderId riderId,
                                List<ChecklistEntry> items, Instant alertSentAt,
                                Instant createdAt, Instant updatedAt) {
        this.id          = id;
        this.eventId     = eventId;
        this.riderId     = riderId;
        this.items       = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.alertSentAt = alertSentAt;
        this.createdAt   = createdAt;
        this.updatedAt   = updatedAt;
    }

    // ── Factories ────────────────────────────────────────────────────────────

    public static EventRiderChecklist create(EventId eventId, TechnicalRider rider) {
        Instant now   = Instant.now();
        List<ChecklistEntry> entries = generateEntries(rider);
        return new EventRiderChecklist(ChecklistId.generate(), eventId, rider.getId(),
                entries, null, now, now);
    }

    public static EventRiderChecklist reconstitute(ChecklistId id, EventId eventId, RiderId riderId,
                                                    List<ChecklistEntry> items, Instant alertSentAt,
                                                    Instant createdAt, Instant updatedAt) {
        return new EventRiderChecklist(id, eventId, riderId, items, alertSentAt, createdAt, updatedAt);
    }

    // ── Behavior ─────────────────────────────────────────────────────────────

    public void confirmEntry(UUID entryId, String notes) {
        int idx = findEntryIndex(entryId);
        items.set(idx, items.get(idx).confirm(notes));
        updatedAt = Instant.now();
    }

    public void unconfirmEntry(UUID entryId) {
        int idx = findEntryIndex(entryId);
        items.set(idx, items.get(idx).unconfirm());
        updatedAt = Instant.now();
    }

    public boolean isFullyConfirmed() {
        return items.stream().filter(ChecklistEntry::required).allMatch(ChecklistEntry::confirmed);
    }

    public int completionPercent() {
        long required  = items.stream().filter(ChecklistEntry::required).count();
        if (required == 0) return 100;
        long confirmed = items.stream().filter(e -> e.required() && e.confirmed()).count();
        return (int) (confirmed * 100L / required);
    }

    public void markAlertSent(Instant now) {
        this.alertSentAt = now;
        this.updatedAt   = now;
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private int findEntryIndex(UUID entryId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).entryId().equals(entryId)) return i;
        }
        throw new ChecklistEntryNotFoundException(entryId.toString());
    }

    private static List<ChecklistEntry> generateEntries(TechnicalRider rider) {
        List<ChecklistEntry> entries = new ArrayList<>();

        // Sound system entries
        if (rider.getSoundSystem() != null) {
            SoundSystemRequirements ss = rider.getSoundSystem();
            entries.add(new ChecklistEntry(UUID.randomUUID(), BacklineCategory.SOUND,
                    "Mesa FOH " + (ss.consoleBrand() != null ? ss.consoleBrand() : "") +
                    " — " + (ss.consoleChannels() != null ? ss.consoleChannels() + " canales" : ""),
                    true, false, null, null));
            if (ss.monitorMixes() != null && ss.monitorMixes() > 0) {
                entries.add(new ChecklistEntry(UUID.randomUUID(), BacklineCategory.SOUND,
                        "Sistema de monitores — " + ss.monitorMixes() + " mezclas",
                        true, false, null, null));
            }
            if (ss.paDescription() != null && !ss.paDescription().isBlank()) {
                entries.add(new ChecklistEntry(UUID.randomUUID(), BacklineCategory.SOUND,
                        "PA: " + ss.paDescription(), true, false, null, null));
            }
        }

        // Backline items
        for (BacklineItem item : rider.getBacklineItems()) {
            String desc = item.description();
            if (item.brand() != null) desc += " (" + item.brand() + (item.model() != null ? " " + item.model() : "") + ")";
            entries.add(new ChecklistEntry(UUID.randomUUID(), item.category(), desc,
                    item.required(), false, null, null));
        }

        // Hospitality entries
        if (rider.getHospitality() != null) {
            HospitalityRequirements h = rider.getHospitality();
            if (h.dressingRoomCapacity() != null) {
                entries.add(new ChecklistEntry(UUID.randomUUID(), BacklineCategory.HOSPITALITY,
                        "Camerino para " + h.dressingRoomCapacity() + " personas",
                        false, false, null, null));
            }
            if (h.waterBottlesOnStage() != null && h.waterBottlesOnStage() > 0) {
                entries.add(new ChecklistEntry(UUID.randomUUID(), BacklineCategory.HOSPITALITY,
                        h.waterBottlesOnStage() + " botellas de agua en escenario",
                        false, false, null, null));
            }
            if (h.cateringNotes() != null && !h.cateringNotes().isBlank()) {
                entries.add(new ChecklistEntry(UUID.randomUUID(), BacklineCategory.HOSPITALITY,
                        "Catering: " + h.cateringNotes(), false, false, null, null));
            }
            if (h.passesCount() != null && h.passesCount() > 0) {
                entries.add(new ChecklistEntry(UUID.randomUUID(), BacklineCategory.HOSPITALITY,
                        h.passesCount() + " acreditaciones / free passes",
                        false, false, null, null));
            }
        }

        // Transport entry
        if (rider.getTransport() != null) {
            TransportRequirements t = rider.getTransport();
            if (t.vehicleType() != null && !t.vehicleType().isBlank()) {
                String desc = "Transporte: " + t.vehicleType();
                if (t.passengerCapacity() != null) desc += " (" + t.passengerCapacity() + " personas)";
                entries.add(new ChecklistEntry(UUID.randomUUID(), BacklineCategory.TRANSPORT,
                        desc, false, false, null, null));
            }
        }

        return entries;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public ChecklistId           getId()          { return id; }
    public EventId               getEventId()     { return eventId; }
    public RiderId               getRiderId()     { return riderId; }
    public List<ChecklistEntry>  getItems()       { return List.copyOf(items); }
    public Instant               getAlertSentAt() { return alertSentAt; }
    public Instant               getCreatedAt()   { return createdAt; }
    public Instant               getUpdatedAt()   { return updatedAt; }
}
