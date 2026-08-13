package com.gresk.modules.journal.infrastructure.persistence.entity;

import com.gresk.modules.journal.domain.model.DatePrecision;
import com.gresk.modules.journal.domain.model.JournalEntrySource;
import com.gresk.modules.journal.domain.model.JournalVisibility;
import com.gresk.shared.domain.MusicGenre;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "journal_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "artist_name", length = 150)
    private String artistName;

    @Column(name = "artist_id")
    private UUID artistId;

    @Column(name = "approx_date", nullable = false)
    private LocalDate approxDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "date_precision", length = 10, nullable = false)
    private DatePrecision datePrecision;

    @Column(name = "venue_name", length = 150)
    private String venueName;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "rating_criteria", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String ratingCriteriaJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", length = 30)
    private MusicGenre genre;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", length = 10, nullable = false)
    private JournalVisibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 20, nullable = false)
    private JournalEntrySource source;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<JournalEntryMediaEntity> media = new ArrayList<>();
}
