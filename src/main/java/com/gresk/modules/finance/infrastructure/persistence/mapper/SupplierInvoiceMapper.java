package com.gresk.modules.finance.infrastructure.persistence.mapper;

import com.gresk.modules.finance.domain.model.SupplierInvoice;
import com.gresk.modules.finance.domain.model.SupplierInvoiceId;
import com.gresk.modules.finance.domain.model.SupplierInvoiceStatus;
import com.gresk.modules.finance.domain.model.valueobject.CostLineId;
import com.gresk.modules.finance.domain.model.valueobject.InvoiceParty;
import com.gresk.modules.finance.infrastructure.persistence.entity.SupplierInvoiceEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

@Component
public class SupplierInvoiceMapper {

    public SupplierInvoice toDomain(SupplierInvoiceEntity e) {
        InvoiceParty supplier = new InvoiceParty(
                e.getSupplierName(), e.getSupplierTaxId(), e.getSupplierAddress(),
                e.getSupplierCountry(), e.getSupplierEmail());

        return SupplierInvoice.reconstitute(
                SupplierInvoiceId.of(e.getId()),
                PromoterId.of(e.getPromoterId()),
                e.getLinkedEventId(),
                CostLineId.of(e.getLinkedCostLineId()),
                supplier,
                e.getSupplierInvoiceNumber(),
                new Money(e.getAmount(), e.getCurrency()),
                new Money(e.getTaxAmount(), e.getCurrency()),
                new Money(e.getTotal(), e.getCurrency()),
                e.getIssueDate(), e.getDueDate(),
                e.getBudgetedAmountSnapshot() != null ? new Money(e.getBudgetedAmountSnapshot(), e.getCurrency()) : null,
                e.getDeviationPercentage(),
                e.isDeviationExceedsThreshold(),
                SupplierInvoiceStatus.valueOf(e.getStatus()),
                e.getDisputeReason());
    }

    public SupplierInvoiceEntity toEntity(SupplierInvoice invoice) {
        InvoiceParty s = invoice.getSupplier();
        return SupplierInvoiceEntity.builder()
                .id(invoice.getId().value())
                .promoterId(invoice.getPromoterId().value())
                .linkedEventId(invoice.getLinkedEventId())
                .linkedCostLineId(invoice.getLinkedCostLineId().value())
                .supplierName(s != null ? s.name() : null)
                .supplierTaxId(s != null ? s.taxId() : null)
                .supplierAddress(s != null ? s.address() : null)
                .supplierCountry(s != null ? s.country() : null)
                .supplierEmail(s != null ? s.email() : null)
                .supplierInvoiceNumber(invoice.getSupplierInvoiceNumber())
                .amount(invoice.getAmount().amount())
                .taxAmount(invoice.getTaxAmount().amount())
                .total(invoice.getTotal().amount())
                .currency(invoice.getTotal().currency())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .budgetedAmountSnapshot(invoice.getBudgetedAmountSnapshot() != null ? invoice.getBudgetedAmountSnapshot().amount() : null)
                .deviationPercentage(invoice.getDeviationPercentage())
                .deviationExceedsThreshold(invoice.isDeviationExceedsThreshold())
                .status(invoice.getStatus().name())
                .disputeReason(invoice.getDisputeReason())
                .build();
    }
}
