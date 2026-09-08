package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.IssueInvoiceCommand;
import com.gresk.modules.finance.domain.model.Invoice;
import com.gresk.modules.finance.domain.model.valueobject.InvoiceLine;
import com.gresk.modules.finance.domain.model.valueobject.InvoiceParty;
import com.gresk.modules.finance.domain.port.out.InvoiceRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class IssueInvoiceUseCase {

    private final InvoiceRepositoryPort invoiceRepository;

    public Invoice execute(IssueInvoiceCommand cmd) {
        PromoterId promoterId = PromoterId.of(cmd.promoterId());
        String currency = cmd.currency() != null ? cmd.currency() : "EUR";

        InvoiceParty recipient = new InvoiceParty(
                cmd.recipientName(), cmd.recipientTaxId(), cmd.recipientAddress(),
                cmd.recipientCountry(), cmd.recipientEmail());

        var lines = cmd.lines().stream()
                .map(l -> new InvoiceLine(l.description(), l.quantity(),
                        new Money(l.unitPriceAmount(), currency), l.taxRatePercentage()))
                .toList();

        String invoiceNumber = generateInvoiceNumber(promoterId);

        Invoice invoice = Invoice.create(promoterId, UUID.fromString(cmd.linkedEventId()), invoiceNumber,
                recipient, lines, cmd.issueDate(), cmd.dueDate());
        invoice.issue();

        return invoiceRepository.save(invoice);
    }

    private String generateInvoiceNumber(PromoterId promoterId) {
        int year  = Year.now().getValue();
        int count = invoiceRepository.countByPromoterIdForYear(promoterId, year);
        return "INV-" + year + "-" + String.format("%03d", count + 1);
    }
}
