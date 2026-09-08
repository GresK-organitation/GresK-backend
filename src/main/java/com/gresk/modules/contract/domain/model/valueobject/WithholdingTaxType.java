package com.gresk.modules.contract.domain.model.valueobject;

/** IRNR = Impuesto sobre la Renta de No Residentes (art. 13 LIRNR, convenio UE). */
public enum WithholdingTaxType {
    NONE,
    IRNR_NON_RESIDENT,
    IRPF_DOMESTIC,
    EU_REVERSE_CHARGE
}
