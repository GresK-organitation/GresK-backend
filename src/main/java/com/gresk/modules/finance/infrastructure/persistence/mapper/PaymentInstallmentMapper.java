package com.gresk.modules.finance.infrastructure.persistence.mapper;

import com.gresk.modules.finance.domain.model.InstallmentPurpose;
import com.gresk.modules.finance.domain.model.InstallmentStatus;
import com.gresk.modules.finance.domain.model.PaymentInstallment;
import com.gresk.modules.finance.domain.model.PaymentInstallmentId;
import com.gresk.modules.finance.domain.model.valueobject.WithholdingApplication;
import com.gresk.modules.finance.domain.model.valueobject.WithholdingKind;
import com.gresk.modules.finance.infrastructure.persistence.entity.PaymentInstallmentEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

@Component
public class PaymentInstallmentMapper {

    public PaymentInstallment toDomain(PaymentInstallmentEntity e) {
        WithholdingApplication withholding = null;
        if (e.getWhtKind() != null) {
            withholding = new WithholdingApplication(
                    WithholdingKind.valueOf(e.getWhtKind()), e.getWhtRatePercentage(),
                    e.getWhtTaxBase() != null ? new Money(e.getWhtTaxBase(), e.getCurrency()) : null,
                    e.getWhtWithheldAmount() != null ? new Money(e.getWhtWithheldAmount(), e.getCurrency()) : null,
                    e.getWhtExemptionReason());
        }

        return PaymentInstallment.reconstitute(
                PaymentInstallmentId.of(e.getId()),
                PromoterId.of(e.getPromoterId()),
                InstallmentPurpose.valueOf(e.getPurpose()),
                e.getLinkedContractId(),
                e.getLinkedSettlementId(),
                new Money(e.getAmount(), e.getCurrency()),
                e.getDescription(),
                e.getDueDate(),
                InstallmentStatus.valueOf(e.getStatus()),
                e.getPaidDate(),
                e.getPaymentMethod(),
                withholding);
    }

    public PaymentInstallmentEntity toEntity(PaymentInstallment i) {
        WithholdingApplication w = i.getWithholding();
        return PaymentInstallmentEntity.builder()
                .id(i.getId().value())
                .promoterId(i.getPromoterId().value())
                .purpose(i.getPurpose().name())
                .linkedContractId(i.getLinkedContractId())
                .linkedSettlementId(i.getLinkedSettlementId())
                .amount(i.getAmount().amount())
                .currency(i.getAmount().currency())
                .description(i.getDescription())
                .dueDate(i.getDueDate())
                .status(i.getStatus().name())
                .paidDate(i.getPaidDate())
                .paymentMethod(i.getPaymentMethod())
                .whtKind(w != null ? w.kind().name() : null)
                .whtRatePercentage(w != null ? w.ratePercentage() : null)
                .whtTaxBase(w != null && w.taxBase() != null ? w.taxBase().amount() : null)
                .whtWithheldAmount(w != null && w.withheldAmount() != null ? w.withheldAmount().amount() : null)
                .whtExemptionReason(w != null ? w.exemptionReason() : null)
                .build();
    }
}
