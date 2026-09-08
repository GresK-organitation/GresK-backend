package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.GenerateInvoicePdfCommand;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.exception.InvoiceNotFoundException;
import com.gresk.modules.finance.domain.model.Invoice;
import com.gresk.modules.finance.domain.model.InvoiceId;
import com.gresk.modules.finance.domain.port.out.InvoicePdfRendererPort;
import com.gresk.modules.finance.domain.port.out.InvoiceRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenerateInvoicePdfUseCase {

    private final InvoiceRepositoryPort   invoiceRepository;
    private final InvoicePdfRendererPort  pdfRenderer;

    public byte[] execute(GenerateInvoicePdfCommand cmd) {
        Invoice invoice = invoiceRepository.findById(InvoiceId.of(cmd.invoiceId()))
                .orElseThrow(() -> new InvoiceNotFoundException(cmd.invoiceId()));

        if (!invoice.getPromoterId().equals(PromoterId.of(cmd.promoterId()))) {
            throw new FinanceResourceNotOwnedException();
        }

        return pdfRenderer.render(invoice);
    }
}
