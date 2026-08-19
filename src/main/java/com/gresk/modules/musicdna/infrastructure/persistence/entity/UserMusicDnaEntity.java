package com.gresk.modules.musicdna.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_music_dna")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMusicDnaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "intensidad_score", nullable = false)
    private BigDecimal intensidadScore;

    @Column(name = "intensidad_label", nullable = false, length = 30)
    private String intensidadLabel;

    @Column(name = "diversidad_score", nullable = false)
    private BigDecimal diversidadScore;

    @Column(name = "diversidad_label", nullable = false, length = 30)
    private String diversidadLabel;

    @Column(name = "criticidad_score", nullable = false)
    private BigDecimal criticidadScore;

    @Column(name = "criticidad_label", nullable = false, length = 30)
    private String criticidadLabel;

    @Column(name = "localismo_score", nullable = false)
    private BigDecimal localismoScore;

    @Column(name = "localismo_label", nullable = false, length = 30)
    private String localismoLabel;

    @Column(name = "antiguedad_score", nullable = false)
    private BigDecimal antiguedadScore;

    @Column(name = "antiguedad_label", nullable = false, length = 30)
    private String antiguedadLabel;

    @Column(name = "autenticidad_score", nullable = false)
    private BigDecimal autenticidadScore;

    @Column(name = "autenticidad_label", nullable = false, length = 30)
    private String autenticidadLabel;

    @Column(name = "summary_phrase", nullable = false, length = 200)
    private String summaryPhrase;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt;
}
