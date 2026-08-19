package com.gresk.modules.musicdna.domain.model;

import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignals;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MusicDnaFormulasTest {

    private static MusicDnaSignals signals(long reviewCount, long journalCount,
                                            long writtenReviewCount, long writtenJournalCount,
                                            long totalReviewLikes, long totalCustomCriteriaUsage,
                                            int distinctGenresMin2,
                                            long reviewLocalMatches, long journalLocalMatches,
                                            LocalDate oldestDocumentedDate, Instant userCreatedAt) {
        return new MusicDnaSignals(reviewCount, journalCount, writtenReviewCount, writtenJournalCount,
                totalReviewLikes, totalCustomCriteriaUsage, distinctGenresMin2,
                reviewLocalMatches, journalLocalMatches, oldestDocumentedDate, userCreatedAt);
    }

    @Test
    void intensidad_divideDocumentadosEntreMesesActivo() {
        Instant createdTwoMonthsAgo = LocalDate.now(ZoneOffset.UTC).minusMonths(2).atStartOfDay(ZoneOffset.UTC).toInstant();
        MusicDnaSignals signals = signals(4, 2, 0, 0, 0, 0, 0, 0, 0, null, createdTwoMonthsAgo);

        BigDecimal result = MusicDnaFormulas.intensidad(signals);

        assertEquals(0, new BigDecimal("3.0000000000").compareTo(result));
    }

    @Test
    void intensidad_usaMinimoUnMesParaUsuariosNuevos() {
        MusicDnaSignals signals = signals(3, 0, 0, 0, 0, 0, 0, 0, 0, null, Instant.now());

        BigDecimal result = MusicDnaFormulas.intensidad(signals);

        assertEquals(0, new BigDecimal("3.0000000000").compareTo(result));
    }

    @Test
    void diversidad_divideGenerosEntreRaizDelTotal() {
        MusicDnaSignals signals = signals(3, 1, 0, 0, 0, 0, 2, 0, 0, null, Instant.now());

        BigDecimal result = MusicDnaFormulas.diversidad(signals);

        assertEquals(0, new BigDecimal("1.0000000000").compareTo(result));
    }

    @Test
    void criticidad_combinaLosTresTerminosPonderados() {
        // writtenDocs=3/4, utilidad=3/3, criteriosCustom=3/3 -> 0.75*0.4 + 1.0*0.4 + 1.0*0.2 = 0.9
        MusicDnaSignals signals = signals(3, 1, 2, 1, 3, 3, 0, 0, 0, null, Instant.now());

        BigDecimal result = MusicDnaFormulas.criticidad(signals);

        assertEquals(0, new BigDecimal("0.9000000000").compareTo(result));
    }

    @Test
    void criticidad_noExplotaSinDocumentosEscritos() {
        MusicDnaSignals signals = signals(3, 1, 0, 0, 0, 0, 0, 0, 0, null, Instant.now());

        BigDecimal result = MusicDnaFormulas.criticidad(signals);

        assertEquals(0, BigDecimal.ZERO.compareTo(result));
    }

    @Test
    void localismo_sumaCoincidenciasDeReviewYJournal() {
        MusicDnaSignals signals = signals(3, 1, 0, 0, 0, 0, 0, 2, 0, null, Instant.now());

        BigDecimal result = MusicDnaFormulas.localismo(signals);

        assertEquals(0, new BigDecimal("0.5000000000").compareTo(result));
    }

    @Test
    void antiguedadYears_convierteDiasAAniosConCapDeImportacionYaAplicado() {
        LocalDate threeYearsAgo = LocalDate.now(ZoneOffset.UTC).minusYears(3);
        MusicDnaSignals signals = signals(1, 0, 0, 0, 0, 0, 0, 0, 0, threeYearsAgo, Instant.now());

        BigDecimal result = MusicDnaFormulas.antiguedadYears(signals);

        assertTrue(result.compareTo(new BigDecimal("2.9")) > 0);
        assertTrue(result.compareTo(new BigDecimal("3.1")) < 0);
    }

    @Test
    void antiguedadYears_devuelveCeroSinFechaDocumentada() {
        MusicDnaSignals signals = signals(0, 0, 0, 0, 0, 0, 0, 0, 0, null, Instant.now());

        BigDecimal result = MusicDnaFormulas.antiguedadYears(signals);

        assertEquals(0, BigDecimal.ZERO.compareTo(result));
    }

    @Test
    void autenticidad_esProporcionDeReviewsVerificadas() {
        MusicDnaSignals signals = signals(3, 1, 0, 0, 0, 0, 0, 0, 0, null, Instant.now());

        BigDecimal result = MusicDnaFormulas.autenticidad(signals);

        assertEquals(0, new BigDecimal("0.7500000000").compareTo(result));
    }

    @Test
    void autenticidad_devuelveCeroSinContenidoDocumentado() {
        MusicDnaSignals signals = signals(0, 0, 0, 0, 0, 0, 0, 0, 0, null, Instant.now());

        BigDecimal result = MusicDnaFormulas.autenticidad(signals);

        assertEquals(0, BigDecimal.ZERO.compareTo(result));
    }
}
