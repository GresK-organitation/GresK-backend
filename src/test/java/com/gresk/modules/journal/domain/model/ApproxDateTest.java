package com.gresk.modules.journal.domain.model;

import com.gresk.modules.journal.domain.exception.InvalidApproxDateException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApproxDateTest {

    @Test
    void exact_guardaLaFechaConPrecisionExacta() {
        LocalDate date = LocalDate.of(2019, 7, 12);

        ApproxDate approxDate = ApproxDate.exact(date);

        assertEquals(date, approxDate.value());
        assertEquals(DatePrecision.EXACT_DATE, approxDate.precision());
    }

    @Test
    void ofMonth_normalizaAlPrimerDiaDelMes() {
        ApproxDate approxDate = ApproxDate.ofMonth(YearMonth.of(2019, 3));

        assertEquals(LocalDate.of(2019, 3, 1), approxDate.value());
        assertEquals(DatePrecision.MONTH, approxDate.precision());
    }

    @Test
    void ofYear_normalizaAlPrimerDiaDelAnio() {
        ApproxDate approxDate = ApproxDate.ofYear(2019);

        assertEquals(LocalDate.of(2019, 1, 1), approxDate.value());
        assertEquals(DatePrecision.YEAR, approxDate.precision());
    }

    @Test
    void rechazaFechasEnElFuturo() {
        LocalDate future = LocalDate.now().plusDays(1);

        assertThrows(InvalidApproxDateException.class, () -> ApproxDate.exact(future));
    }
}
