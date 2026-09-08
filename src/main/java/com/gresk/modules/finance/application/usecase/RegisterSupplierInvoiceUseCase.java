package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.RegisterSupplierInvoiceCommand;
import com.gresk.modules.finance.domain.exception.EventFinancialPlanNotFoundException;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.model.EventFinancialPlan;
import com.gresk.modules.finance.domain.model.SupplierInvoice;
import com.gresk.modules.finance.domain.model.valueobject.CostLineId;
import com.gresk.modules.finance.domain.model.valueobject.InvoiceParty;
import com.gresk.modules.finance.domain.port.out.EventFinancialPlanRepositoryPort;
import com.gresk.modules.finance.domain.port.out.SupplierInvoiceRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterSupplierInvoiceUseCase {

    private final SupplierInvoiceRepositoryPort    supplierInvoiceRepository;
    private final EventFinancialPlanRepositoryPort planRepository;

    public SupplierInvoice execute(RegisterSupplierInvoiceCommand cmd) {
        PromoterId promoterId = PromoterId.of(cmd.promoterId());
        UUID linkedEventId = UUID.fromString(cmd.linkedEventId());

        EventFinancialPlan plan = planRepository.findByLinkedEventId(linkedEventId)
                .orElseThrow(() -> new EventFinancialPlanNotFoundException(cmd.linkedEventId()));

        if (!plan.getPromoterId().equals(promoterId)) {
            throw new FinanceResourceNotOwnedException();
        }

        String currency = cmd.currency() != null ? cmd.currency() : "EUR";
        InvoiceParty supplier = new InvoiceParty(
                cmd.supplierName(), cmd.supplierTaxId(), cmd.supplierAddress(),
                cmd.supplierCountry(), cmd.supplierEmail());

        SupplierInvoice invoice = SupplierInvoice.associateToBudget(
                promoterId, linkedEventId, CostLineId.of(cmd.linkedCostLineId()), supplier,
                cmd.supplierInvoiceNumber(), new Money(cmd.amount(), currency),
                new Money(cmd.taxAmount(), currency), cmd.issueDate(), cmd.dueDate(), plan);

        return supplierInvoiceRepository.save(invoice);
    }
}
