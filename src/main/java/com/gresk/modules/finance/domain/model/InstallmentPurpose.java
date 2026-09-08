package com.gresk.modules.finance.domain.model;

public enum InstallmentPurpose {
    /** Anticipo (ej. 50% al firmar contrato), ligado a linkedContractId. */
    DEPOSIT,
    /** Saldo tras una liquidación aprobada, ligado a linkedSettlementId. Se crea automáticamente. */
    BALANCE,
    /** Pago suelto no ligado a un contrato/liquidación concreto. */
    AD_HOC
}
