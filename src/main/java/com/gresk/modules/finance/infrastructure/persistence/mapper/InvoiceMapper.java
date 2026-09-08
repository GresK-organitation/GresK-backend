package com.gresk.modules.finance.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.finance.domain.model.Invoice;
import com.gresk.modules.finance.domain.model.InvoiceId;
import com.gresk.modules.finance.domain.model.InvoiceStatus;
import com.gresk.modules.finance.domain.model.valueobject.InvoiceLine;
import com.gresk.modules.finance.domain.model.valueobject.InvoiceParty;
import com.gresk.modules.finance.infrastructure.persistence.entity.InvoiceEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InvoiceMapper {

    private final ObjectMapper objectMapper;

    public Invoice toDomain(InvoiceEntity e) {
        InvoiceParty recipient = new InvoiceParty(
                e.getRecipientName(), e.getRecipientTaxId(), e.getRecipientAddress(),
                e.getRecipientCountry(), e.getRecipientEmail());

        return Invoice.reconstitute(
                InvoiceId.of(e.getId()),
                PromoterId.of(e.getPromoterId()),
                e.getLinkedEventId(),
                e.getInvoiceNumber(),
                recipient,
                deserializeLines(e.getLinesJson()),
                new Money(e.getSubtotal(), e.getCurrency()),
                new Money(e.getTaxAmount(), e.getCurrency()),
                new Money(e.getTotal(), e.getCurrency()),
                e.getIssueDate(), e.getDueDate(),
                InvoiceStatus.valueOf(e.getStatus()),
                e.getPdfAssetId());
    }

    public InvoiceEntity toEntity(Invoice invoice) {
        InvoiceParty r = invoice.getRecipient();
        return InvoiceEntity.builder()
                .id(invoice.getId().value())
                .promoterId(invoice.getPromoterId().value())
                .linkedEventId(invoice.getLinkedEventId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .recipientName(r != null ? r.name() : null)
                .recipientTaxId(r != null ? r.taxId() : null)
                .recipientAddress(r != null ? r.address() : null)
                .recipientCountry(r != null ? r.country() : null)
                .recipientEmail(r != null ? r.email() : null)
                .linesJson(serializeLines(invoice.getLines()))
                .subtotal(invoice.getSubtotal().amount())
                .taxAmount(invoice.getTaxAmount().amount())
                .total(invoice.getTotal().amount())
                .currency(invoice.getTotal().currency())
                .status(invoice.getStatus().name())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .pdfAssetId(invoice.getPdfAssetId())
                .build();
    }

    private String serializeLines(List<InvoiceLine> lines) {
        try {
            return objectMapper.writeValueAsString(lines);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<InvoiceLine> deserializeLines(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
