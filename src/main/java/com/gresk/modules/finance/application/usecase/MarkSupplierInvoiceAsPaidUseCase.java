package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.MarkSupplierInvoiceAsPaidCommand;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.exception.SupplierInvoiceNotFoundException;
import com.gresk.modules.finance.domain.model.SupplierInvoice;
import com.gresk.modules.finance.domain.model.SupplierInvoiceId;
import com.gresk.modules.finance.domain.port.out.SupplierInvoiceRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkSupplierInvoiceAsPaidUseCase {

    private final SupplierInvoiceRepositoryPort supplierInvoiceRepository;

    public SupplierInvoice execute(MarkSupplierInvoiceAsPaidCommand cmd) {
        SupplierInvoice invoice = supplierInvoiceRepository.findById(SupplierInvoiceId.of(cmd.supplierInvoiceId()))
                .orElseThrow(() -> new SupplierInvoiceNotFoundException(cmd.supplierInvoiceId()));

        if (!invoice.getPromoterId().equals(PromoterId.of(cmd.promoterId()))) {
            throw new FinanceResourceNotOwnedException();
        }

        invoice.markPaid();
        return supplierInvoiceRepository.save(invoice);
    }
}
