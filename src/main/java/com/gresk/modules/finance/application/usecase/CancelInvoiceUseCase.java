package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.CancelInvoiceCommand;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.exception.InvoiceNotFoundException;
import com.gresk.modules.finance.domain.model.Invoice;
import com.gresk.modules.finance.domain.model.InvoiceId;
import com.gresk.modules.finance.domain.port.out.InvoiceRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CancelInvoiceUseCase {

    private final InvoiceRepositoryPort invoiceRepository;

    public void execute(CancelInvoiceCommand cmd) {
        Invoice invoice = invoiceRepository.findById(InvoiceId.of(cmd.invoiceId()))
                .orElseThrow(() -> new InvoiceNotFoundException(cmd.invoiceId()));

        if (!invoice.getPromoterId().equals(PromoterId.of(cmd.promoterId()))) {
            throw new FinanceResourceNotOwnedException();
        }

        invoice.cancel();
        invoiceRepository.save(invoice);
    }
}
