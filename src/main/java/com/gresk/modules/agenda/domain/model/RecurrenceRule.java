package com.gresk.modules.agenda.domain.model;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Value object que encapsula la regla de repetición de una entrada de agenda.
 * Inspirado en RFC 5545 (iCalendar RRULE), simplificado para los casos de uso de GresK.
 *
 * <p>Restricciones:
 * <ul>
 *   <li>{@code interval} ≥ 1</li>
 *   <li>{@code count} y {@code until} son mutuamente excluyentes (puede haber ninguno)</li>
 *   <li>{@code byDay} solo aplica a {@link RecurrenceFrequency#WEEKLY}</li>
 *   <li>{@code byMonthDay} solo aplica a {@link RecurrenceFrequency#MONTHLY}</li>
 * </ul>
 */
public record RecurrenceRule(
        RecurrenceFrequency frequency,
        int interval,
        Integer count,
        Instant until,
        Set<DayOfWeek> byDay,
        Integer byMonthDay
) {

    public RecurrenceRule {
        Objects.requireNonNull(frequency, "RecurrenceRule frequency must not be null");
        if (interval < 1) throw new IllegalArgumentException("RecurrenceRule interval must be >= 1");
        if (count != null && count < 1) throw new IllegalArgumentException("RecurrenceRule count must be >= 1");
        if (byDay == null) byDay = Set.of();
    }

    // ── Expansión ────────────────────────────────────────────────────────────

    /**
     * Genera la lista de instantes (UTC) en que ocurre esta serie dentro del rango [rangeFrom, rangeTo].
     * No incluye ocurrencias anteriores a {@code seriesStart}.
     *
     * @param seriesStart fecha/hora de inicio de la entrada maestra (UTC)
     * @param rangeFrom   inicio del rango de consulta (inclusive)
     * @param rangeTo     fin del rango de consulta (inclusive)
     * @return ocurrencias ordenadas cronológicamente dentro del rango
     */
    public List<Instant> expand(Instant seriesStart, Instant rangeFrom, Instant rangeTo) {
        Objects.requireNonNull(seriesStart, "seriesStart must not be null");
        Objects.requireNonNull(rangeFrom,   "rangeFrom must not be null");
        Objects.requireNonNull(rangeTo,     "rangeTo must not be null");

        List<Instant> result = new ArrayList<>();
        int generated = 0;

        if (frequency == RecurrenceFrequency.WEEKLY && !byDay.isEmpty()) {
            result.addAll(expandWeeklyWithDays(seriesStart, rangeFrom, rangeTo, generated));
        } else {
            result.addAll(expandSimple(seriesStart, rangeFrom, rangeTo));
        }
        return result;
    }

    // ── Expansión semanal con días específicos ───────────────────────────────

    private List<Instant> expandWeeklyWithDays(Instant seriesStart, Instant rangeFrom,
                                                Instant rangeTo, int initialGenerated) {
        List<Instant> result = new ArrayList<>();
        int generated = initialGenerated;

        ZoneOffset utc = ZoneOffset.UTC;
        ZonedDateTime startZdt = seriesStart.atZone(utc);

        List<DayOfWeek> sortedDays = byDay.stream()
                .sorted(Comparator.comparingInt(DayOfWeek::getValue))
                .toList();

        // Base del primer ciclo semanal: lunes de la semana en que empieza la serie
        ZonedDateTime weekBase = startZdt
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(startZdt.getHour())
                .withMinute(startZdt.getMinute())
                .withSecond(0)
                .withNano(0);

        outer:
        while (true) {
            for (DayOfWeek day : sortedDays) {
                ZonedDateTime occurrence = weekBase.with(TemporalAdjusters.nextOrSame(day));
                Instant occ = occurrence.toInstant();

                if (occ.isBefore(seriesStart)) continue;       // días antes del inicio real
                if (occ.isAfter(rangeTo))       break outer;   // ya fuera del rango
                if (!isBeforeUntil(occ))        break outer;   // pasada la fecha fin de serie
                if (isLimitReached(generated))  break outer;   // alcanzado el máximo

                if (!occ.isBefore(rangeFrom)) {
                    result.add(occ);
                }
                generated++;
            }
            weekBase = weekBase.plusWeeks((long) interval);
        }

        return result;
    }

    // ── Expansión simple (DAILY, WEEKLY sin byDay, MONTHLY) ─────────────────

    private List<Instant> expandSimple(Instant seriesStart, Instant rangeFrom, Instant rangeTo) {
        List<Instant> result = new ArrayList<>();
        int generated = 0;

        ZoneOffset utc = ZoneOffset.UTC;
        ZonedDateTime current = seriesStart.atZone(utc);

        while (true) {
            Instant occ = current.toInstant();

            if (occ.isAfter(rangeTo))      break;
            if (!isBeforeUntil(occ))       break;
            if (isLimitReached(generated)) break;

            if (!occ.isBefore(rangeFrom)) {
                result.add(occ);
            }
            generated++;

            current = switch (frequency) {
                case DAILY  -> current.plusDays(interval);
                case WEEKLY -> current.plusWeeks(interval);
                case MONTHLY -> {
                    ZonedDateTime next = current.plusMonths(interval);
                    if (byMonthDay != null) {
                        int maxDay = next.toLocalDate().lengthOfMonth();
                        yield next.withDayOfMonth(Math.min(byMonthDay, maxDay));
                    }
                    yield next;
                }
            };
        }

        return result;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean isLimitReached(int generated) {
        return count != null && generated >= count;
    }

    private boolean isBeforeUntil(Instant occurrence) {
        return until == null || !occurrence.isAfter(until);
    }
}
