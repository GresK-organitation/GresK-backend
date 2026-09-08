package com.gresk.modules.contract.domain.model.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WithholdingTaxTest {

    @Test
    void noneNoExigeTasa() {
        WithholdingTax none = WithholdingTax.none();
        assertEquals(WithholdingTaxType.NONE, none.type());
    }

    @Test
    void rechazaTasaNegativa() {
        assertThrows(IllegalArgumentException.class, () ->
                new WithholdingTax(WithholdingTaxType.IRNR_NON_RESIDENT, BigDecimal.valueOf(-1),
                        BigDecimal.TEN, BigDecimal.ONE, null));
    }

    @Test
    void rechazaTasaMayorQueCien() {
        assertThrows(IllegalArgumentException.class, () ->
                new WithholdingTax(WithholdingTaxType.IRNR_NON_RESIDENT, BigDecimal.valueOf(101),
                        BigDecimal.TEN, BigDecimal.ONE, null));
    }

    @Test
    void aceptaTasaValidaParaIrnr() {
        WithholdingTax wht = new WithholdingTax(WithholdingTaxType.IRNR_NON_RESIDENT,
                BigDecimal.valueOf(24), BigDecimal.valueOf(1000), BigDecimal.valueOf(240), null);
        assertEquals(BigDecimal.valueOf(24), wht.ratePercentage());
    }
}
