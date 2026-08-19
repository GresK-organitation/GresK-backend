package com.gresk.modules.musicdna.domain.model;

import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignals;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserMusicDnaTest {

    private final UserId userId = UserId.of(UUID.randomUUID());

    @Test
    void calculate_construyeLasSeisDimensionesYLaFraseResumen() {
        Instant createdOneMonthAgo = LocalDate.now(ZoneOffset.UTC).minusMonths(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        MusicDnaSignals signals = new MusicDnaSignals(
                3, 1,           // reviewCount, journalCount -> totalDocumented=4
                2, 1,           // writtenReviewCount, writtenJournalCount
                3, 3,           // totalReviewLikes, totalCustomCriteriaUsage
                2,              // distinctGenresMin2
                2, 0,           // reviewLocalMatches, journalLocalMatches
                LocalDate.now(ZoneOffset.UTC).minusYears(1),
                createdOneMonthAgo
        );

        UserMusicDna dna = UserMusicDna.calculate(userId, signals);

        assertEquals(userId, dna.getUserId());
        assertEquals("Obsesivo", dna.getIntensidad().label());     // 4 docs / 1 mes = 4.0 -> satura en Obsesivo
        assertEquals("Omnívoro", dna.getDiversidad().label());     // 2 géneros / sqrt(4) = 1.0 -> por encima de 0.8
        // La frase resumen contiene exactamente 3 adjetivos separados por " · "
        List<String> phraseParts = List.of(dna.getSummaryPhrase().split(" · "));
        assertEquals(3, phraseParts.size());
    }

    @Test
    void calculate_sinContenidoDevuelveAdnVacioSinDividirPorCero() {
        MusicDnaSignals signals = new MusicDnaSignals(
                0, 0, 0, 0, 0, 0, 0, 0, 0, null, Instant.now());

        UserMusicDna dna = UserMusicDna.calculate(userId, signals);

        assertEquals("Sin datos suficientes", dna.getSummaryPhrase());
        assertEquals(0, dna.getIntensidad().score().compareTo(java.math.BigDecimal.ZERO));
    }

    @Test
    void calculate_laFraseResumenPriorizaLosTresScoresMasAltos() {
        Instant createdLongAgo = LocalDate.now(ZoneOffset.UTC).minusYears(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        // Autenticidad=1.0 (solo reviews) y Antigüedad alta deberían dominar sobre Localismo=0
        MusicDnaSignals signals = new MusicDnaSignals(
                10, 0,
                10, 0,
                0, 0,
                1,
                0, 0,
                LocalDate.now(ZoneOffset.UTC).minusYears(8),
                createdLongAgo
        );

        UserMusicDna dna = UserMusicDna.calculate(userId, signals);

        assertEquals("Irrefutable", dna.getAutenticidad().label());
        assertTrue(dna.getSummaryPhrase().contains("Irrefutable"));
    }
}
