package com.gresk.modules.rider.infrastructure.persistence.entity;

import com.gresk.modules.rider.domain.model.BacklineCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "checklist_entries")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChecklistEntryEntity {

    @Id
    @Column(name = "entry_id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID entryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private EventRiderChecklistEntity checklist;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20, nullable = false)
    private BacklineCategory category;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "required", nullable = false)
    private boolean required;

    @Column(name = "confirmed", nullable = false)
    private boolean confirmed;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "confirmed_notes", columnDefinition = "TEXT")
    private String confirmedNotes;

    public void update(boolean confirmed, Instant confirmedAt, String confirmedNotes) {
        this.confirmed      = confirmed;
        this.confirmedAt    = confirmedAt;
        this.confirmedNotes = confirmedNotes;
    }
}
