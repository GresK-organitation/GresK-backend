package com.gresk.modules.finance.domain.model.valueobject;

import com.gresk.shared.domain.valueobject.Money;

import java.math.BigDecimal;

/**
 * Términos económicos de una liquidación, snapshoteados dentro de un SettlementAgreement
 * en el momento de su creación (patrón Pretix OrderPosition / Stripe Charge inmutable).
 * La combinación de campos requerida depende de {@code type} — calcado del patrón
 * condicional de contract.WithholdingTax.
 */
public record DealTerms(
        SettlementDealType type,
        Money guaranteedAmount,
        BigDecimal artistPercentage,
        Money revenueThreshold
) {
    public DealTerms {
        if (type == null) throw new IllegalArgumentException("type is required");

        boolean needsGuarantee  = type == SettlementDealType.FLAT_FEE
                || type == SettlementDealType.VERSUS
                || type == SettlementDealType.GUARANTEED_MIN_PLUS_PERCENTAGE;
        boolean needsPercentage = type == SettlementDealType.VERSUS
                || type == SettlementDealType.DOOR_SPLIT
                || type == SettlementDealType.GUARANTEED_MIN_PLUS_PERCENTAGE;
        boolean needsThreshold  = type == SettlementDealType.GUARANTEED_MIN_PLUS_PERCENTAGE;

        if (needsGuarantee && guaranteedAmount == null) {
            throw new IllegalArgumentException(type + " requires guaranteedAmount");
        }
        if (!needsGuarantee && guaranteedAmount != null) {
            throw new IllegalArgumentException(type + " does not accept guaranteedAmount");
        }
        if (needsPercentage && artistPercentage == null) {
            throw new IllegalArgumentException(type + " requires artistPercentage");
        }
        if (!needsPercentage && artistPercentage != null) {
            throw new IllegalArgumentException(type + " does not accept artistPercentage");
        }
        if (needsThreshold && revenueThreshold == null) {
            throw new IllegalArgumentException(type + " requires revenueThreshold");
        }
        if (!needsThreshold && revenueThreshold != null) {
            throw new IllegalArgumentException(type + " does not accept revenueThreshold");
        }
        if (artistPercentage != null
                && (artistPercentage.signum() < 0 || artistPercentage.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new IllegalArgumentException("artistPercentage must be between 0 and 100");
        }
    }
}
