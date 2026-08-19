package com.gresk.modules.musicdna.domain.model;

import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * Las 6 fórmulas del ADN Musical, puras y sin dependencias de
 * infraestructura — solo consumen {@link MusicDnaSignals}. Solo se invocan
 * cuando {@code signals.totalDocumented() > 0} (ver {@link UserMusicDna#calculate}).
 */
final class MusicDnaFormulas {

    private static final int SCALE = 10;

    private MusicDnaFormulas() {}

    static BigDecimal intensidad(MusicDnaSignals signals) {
        long monthsActive = Math.max(1, ChronoUnit.MONTHS.between(
                signals.userCreatedAt().atZone(ZoneOffset.UTC).toLocalDate(),
                LocalDate.now(ZoneOffset.UTC)));
        return divide(BigDecimal.valueOf(signals.totalDocumented()), BigDecimal.valueOf(monthsActive));
    }

    static BigDecimal diversidad(MusicDnaSignals signals) {
        if (signals.totalDocumented() == 0) return BigDecimal.ZERO;
        BigDecimal sqrtTotal = BigDecimal.valueOf(Math.sqrt(signals.totalDocumented()));
        return divide(BigDecimal.valueOf(signals.distinctGenresMin2()), sqrtTotal);
    }

    static BigDecimal criticidad(MusicDnaSignals signals) {
        long writtenDocs = signals.writtenReviewCount() + signals.writtenJournalCount();

        BigDecimal writtenRatio = divide(BigDecimal.valueOf(writtenDocs), BigDecimal.valueOf(signals.totalDocumented()));
        BigDecimal utilityRatio = divide(BigDecimal.valueOf(signals.totalReviewLikes()), BigDecimal.valueOf(writtenDocs));
        BigDecimal customRatio  = divide(BigDecimal.valueOf(signals.totalCustomCriteriaUsage()), BigDecimal.valueOf(writtenDocs));

        return writtenRatio.multiply(new BigDecimal("0.4"))
                .add(utilityRatio.multiply(new BigDecimal("0.4")))
                .add(customRatio.multiply(new BigDecimal("0.2")));
    }

    static BigDecimal localismo(MusicDnaSignals signals) {
        long localMatches = signals.reviewLocalMatches() + signals.journalLocalMatches();
        return divide(BigDecimal.valueOf(localMatches), BigDecimal.valueOf(signals.totalDocumented()));
    }

    static BigDecimal antiguedadYears(MusicDnaSignals signals) {
        if (signals.oldestDocumentedDate() == null) return BigDecimal.ZERO;
        long days = ChronoUnit.DAYS.between(signals.oldestDocumentedDate(), LocalDate.now(ZoneOffset.UTC));
        if (days <= 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(days).divide(new BigDecimal("365.25"), SCALE, RoundingMode.HALF_UP);
    }

    static BigDecimal autenticidad(MusicDnaSignals signals) {
        return divide(BigDecimal.valueOf(signals.reviewCount()), BigDecimal.valueOf(signals.totalDocumented()));
    }

    private static BigDecimal divide(BigDecimal numerator, BigDecimal denominator) {
        if (denominator.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return numerator.divide(denominator, SCALE, RoundingMode.HALF_UP);
    }
}
