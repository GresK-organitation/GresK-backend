package com.gresk.modules.journal.domain.model;

import com.gresk.modules.journal.domain.exception.InvalidApproxDateException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

/**
 * A date the user may only remember with partial precision
 * (e.g. "sometime in 2019" or "March 2019" rather than an exact day).
 * {@code value} is always normalized to the first day of the month/year
 * for MONTH/YEAR precision so it can be used as a sortable/filterable lower bound.
 */
public record ApproxDate(LocalDate value, DatePrecision precision) {

    public ApproxDate {
        Objects.requireNonNull(value, "ApproxDate value is required");
        Objects.requireNonNull(precision, "DatePrecision is required");
        if (value.isAfter(LocalDate.now())) {
            throw new InvalidApproxDateException("ApproxDate cannot be in the future");
        }
    }

    public static ApproxDate exact(LocalDate date) {
        return new ApproxDate(date, DatePrecision.EXACT_DATE);
    }

    public static ApproxDate ofMonth(YearMonth yearMonth) {
        return new ApproxDate(yearMonth.atDay(1), DatePrecision.MONTH);
    }

    public static ApproxDate ofYear(int year) {
        return new ApproxDate(LocalDate.of(year, 1, 1), DatePrecision.YEAR);
    }

    public static ApproxDate reconstitute(LocalDate value, DatePrecision precision) {
        return new ApproxDate(value, precision);
    }
}
