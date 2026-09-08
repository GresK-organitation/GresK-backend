package com.gresk.modules.finance.infrastructure.web;

import com.gresk.modules.finance.domain.model.EventFinancialPlan;
import com.gresk.modules.finance.domain.model.Invoice;
import com.gresk.modules.finance.domain.model.PaymentInstallment;
import com.gresk.modules.finance.domain.model.Settlement;
import com.gresk.modules.finance.domain.model.SettlementAgreement;
import com.gresk.modules.finance.domain.model.SupplierInvoice;
import com.gresk.modules.finance.domain.model.valueobject.CostLine;
import com.gresk.modules.finance.domain.model.valueobject.CostSubcategory;
import com.gresk.modules.finance.domain.model.valueobject.EventPnLDashboard;
import com.gresk.modules.finance.domain.model.valueobject.InvoiceLine;
import com.gresk.modules.finance.domain.model.valueobject.WithholdingApplication;
import com.gresk.modules.finance.infrastructure.web.dto.CostLineResponse;
import com.gresk.modules.finance.infrastructure.web.dto.EventFinancialPlanResponse;
import com.gresk.modules.finance.infrastructure.web.dto.EventPnLDashboardResponse;
import com.gresk.modules.finance.infrastructure.web.dto.InvoiceLineResponse;
import com.gresk.modules.finance.infrastructure.web.dto.InvoiceResponse;
import com.gresk.modules.finance.infrastructure.web.dto.PaymentInstallmentResponse;
import com.gresk.modules.finance.infrastructure.web.dto.SettlementAgreementResponse;
import com.gresk.modules.finance.infrastructure.web.dto.SettlementResponse;
import com.gresk.modules.finance.infrastructure.web.dto.SupplierInvoiceResponse;
import com.gresk.modules.finance.infrastructure.web.dto.WithholdingApplicationResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FinanceResponseMapper {

    public EventFinancialPlanResponse toResponse(EventFinancialPlan plan) {
        return new EventFinancialPlanResponse(
                plan.getId().toString(),
                plan.getLinkedEventId().toString(),
                plan.getDeviationThresholdPercentage(),
                plan.getCostLines().stream().map(this::toResponse).toList());
    }

    public CostLineResponse toResponse(CostLine costLine) {
        return new CostLineResponse(
                costLine.id().toString(),
                costLine.category(),
                costLine.subcategory(),
                costLine.description(),
                costLine.budgetedAmount() != null ? costLine.budgetedAmount().amount() : null,
                costLine.variablePercentage(),
                costLine.budgetedAmount() != null ? costLine.budgetedAmount().currency() : null);
    }

    public EventPnLDashboardResponse toResponse(EventPnLDashboard dashboard) {
        Map<CostSubcategory, BigDecimal> breakdown = dashboard.costBreakdown().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().amount()));
        return new EventPnLDashboardResponse(
                dashboard.grossRevenue().amount(),
                dashboard.totalFixedCosts().amount(),
                dashboard.totalVariableCosts().amount(),
                dashboard.netProfit(),
                dashboard.currency(),
                breakdown,
                dashboard.ticketsSold());
    }

    public SettlementAgreementResponse toResponse(SettlementAgreement agreement) {
        var terms = agreement.getDealTerms();
        return new SettlementAgreementResponse(
                agreement.getId().toString(),
                agreement.getLinkedContractId().toString(),
                agreement.getLinkedEventId().toString(),
                terms.type(),
                terms.guaranteedAmount() != null ? terms.guaranteedAmount().amount() : null,
                terms.artistPercentage(),
                terms.revenueThreshold() != null ? terms.revenueThreshold().amount() : null,
                agreement.getContractFeeSnapshot().currency(),
                agreement.getContractFeeSnapshot().amount(),
                agreement.getArtistNameSnapshot(),
                agreement.getStatus().name());
    }

    public SettlementResponse toResponse(Settlement settlement) {
        var b = settlement.getBreakdown();
        return new SettlementResponse(
                settlement.getId().toString(),
                settlement.getSettlementAgreementId().toString(),
                settlement.getLinkedEventId().toString(),
                settlement.getStatus().name(),
                b.dealType(),
                settlement.getArtistPayableAmount().currency(),
                b.grossBoxOffice().amount(),
                b.netBoxOffice().amount(),
                b.ticketingCommissionDeducted() != null ? b.ticketingCommissionDeducted().amount() : null,
                b.guaranteedComponent() != null ? b.guaranteedComponent().amount() : null,
                b.percentageComponent() != null ? b.percentageComponent().amount() : null,
                settlement.getArtistPayableAmount().amount(),
                settlement.getCalculatedAt(),
                settlement.getApprovedAt());
    }

    public PaymentInstallmentResponse toResponse(PaymentInstallment installment) {
        return new PaymentInstallmentResponse(
                installment.getId().toString(),
                installment.getPurpose().name(),
                installment.getLinkedContractId() != null ? installment.getLinkedContractId().toString() : null,
                installment.getLinkedSettlementId() != null ? installment.getLinkedSettlementId().toString() : null,
                installment.getAmount().amount(),
                installment.getAmount().currency(),
                installment.getDescription(),
                installment.getDueDate(),
                installment.getStatus().name(),
                installment.getPaidDate(),
                installment.getPaymentMethod(),
                installment.getWithholding() != null ? toResponse(installment.getWithholding()) : null);
    }

    public WithholdingApplicationResponse toResponse(WithholdingApplication withholding) {
        return new WithholdingApplicationResponse(
                withholding.kind(),
                withholding.ratePercentage(),
                withholding.taxBase() != null ? withholding.taxBase().amount() : null,
                withholding.withheldAmount() != null ? withholding.withheldAmount().amount() : null,
                withholding.taxBase() != null ? withholding.taxBase().currency() : null,
                withholding.exemptionReason());
    }

    public InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId().toString(),
                invoice.getLinkedEventId().toString(),
                invoice.getInvoiceNumber(),
                invoice.getRecipient() != null ? invoice.getRecipient().name() : null,
                invoice.getRecipient() != null ? invoice.getRecipient().taxId() : null,
                invoice.getLines().stream().map(this::toResponse).toList(),
                invoice.getSubtotal().amount(),
                invoice.getTaxAmount().amount(),
                invoice.getTotal().amount(),
                invoice.getTotal().currency(),
                invoice.getStatus().name(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getPdfAssetId());
    }

    public InvoiceLineResponse toResponse(InvoiceLine line) {
        return new InvoiceLineResponse(
                line.description(),
                line.quantity(),
                line.unitPrice().amount(),
                line.taxRatePercentage(),
                line.lineTotal().amount());
    }

    public SupplierInvoiceResponse toResponse(SupplierInvoice invoice) {
        return new SupplierInvoiceResponse(
                invoice.getId().toString(),
                invoice.getLinkedEventId().toString(),
                invoice.getLinkedCostLineId().toString(),
                invoice.getSupplier() != null ? invoice.getSupplier().name() : null,
                invoice.getSupplierInvoiceNumber(),
                invoice.getAmount().amount(),
                invoice.getTaxAmount().amount(),
                invoice.getTotal().amount(),
                invoice.getTotal().currency(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getBudgetedAmountSnapshot() != null ? invoice.getBudgetedAmountSnapshot().amount() : null,
                invoice.getDeviationPercentage(),
                invoice.isDeviationExceedsThreshold(),
                invoice.getStatus().name(),
                invoice.getDisputeReason());
    }
}
