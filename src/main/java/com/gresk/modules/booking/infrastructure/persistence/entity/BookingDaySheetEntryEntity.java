package com.gresk.modules.booking.infrastructure.persistence.entity;

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

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "booking_day_sheet_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDaySheetEntryEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private BookingEntity booking;

    @Column(name = "entry_time", nullable = false)
    private LocalTime entryTime;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
