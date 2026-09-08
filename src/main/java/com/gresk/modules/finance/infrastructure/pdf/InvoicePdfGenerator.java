package com.gresk.modules.finance.infrastructure.pdf;

import com.gresk.modules.finance.domain.model.Invoice;
import com.gresk.modules.finance.domain.model.valueobject.InvoiceLine;
import com.gresk.modules.finance.domain.model.valueobject.InvoiceParty;
import com.gresk.modules.finance.domain.port.out.InvoicePdfRendererPort;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/** Calcado del patrón de ContractPdfGenerator (OpenPDF de bajo nivel), aplicado a facturas de venta. */
@Slf4j
@Component
public class InvoicePdfGenerator implements InvoicePdfRendererPort {

    private static final Font TITLE_FONT   = new Font(Font.HELVETICA, 18, Font.BOLD,  Color.BLACK);
    private static final Font HEADING_FONT = new Font(Font.HELVETICA, 12, Font.BOLD,  new Color(30, 80, 160));
    private static final Font LABEL_FONT   = new Font(Font.HELVETICA, 10, Font.BOLD,  Color.BLACK);
    private static final Font NORMAL_FONT  = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
    private static final Font SMALL_FONT   = new Font(Font.HELVETICA, 8,  Font.NORMAL, Color.DARK_GRAY);
    private static final Color TABLE_BG    = new Color(220, 230, 245);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public byte[] render(Invoice invoice) {
        return generate(invoice);
    }

    private byte[] generate(Invoice invoice) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, invoice);
            addSectionTitle(doc, "CLIENTE");
            addRecipient(doc, invoice.getRecipient());
            addSectionTitle(doc, "CONCEPTOS");
            addLines(doc, invoice);
            addTotals(doc, invoice);
            addFooter(doc);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating invoice PDF for {}: {}", invoice.getInvoiceNumber(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }

    private void addHeader(Document doc, Invoice invoice) throws DocumentException {
        Paragraph title = new Paragraph("FACTURA", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        Paragraph ref = new Paragraph("Nº " + invoice.getInvoiceNumber(), HEADING_FONT);
        ref.setAlignment(Element.ALIGN_CENTER);
        doc.add(ref);

        if (invoice.getIssueDate() != null) {
            Paragraph date = new Paragraph("Fecha de emisión: " + invoice.getIssueDate().format(DATE_FMT), SMALL_FONT);
            date.setAlignment(Element.ALIGN_CENTER);
            doc.add(date);
        }
        doc.add(Chunk.NEWLINE);
        doc.add(new LineSeparator());
        doc.add(Chunk.NEWLINE);
    }

    private void addSectionTitle(Document doc, String title) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph(title, HEADING_FONT));
        doc.add(new LineSeparator());
        doc.add(Chunk.NEWLINE);
    }

    private void addRecipient(Document doc, InvoiceParty recipient) throws DocumentException {
        if (recipient == null) return;
        doc.add(new Paragraph(recipient.name(), NORMAL_FONT));
        if (recipient.taxId() != null)  doc.add(new Paragraph("CIF/NIF: " + recipient.taxId(), NORMAL_FONT));
        if (recipient.address() != null) doc.add(new Paragraph(recipient.address(), NORMAL_FONT));
        if (recipient.country() != null) doc.add(new Paragraph("País: " + recipient.country(), NORMAL_FONT));
    }

    private void addLines(Document doc, Invoice invoice) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1f, 1.2f, 1f, 1.2f});
        addTableHeader(table, "Concepto", "Cant.", "Precio unit.", "IVA %", "Total línea");

        String currency = invoice.getTotal().currency();
        for (InvoiceLine line : invoice.getLines()) {
            table.addCell(cell(line.description(), NORMAL_FONT));
            table.addCell(cell(String.valueOf(line.quantity()), NORMAL_FONT));
            table.addCell(cell(line.unitPrice().amount() + " " + currency, NORMAL_FONT));
            table.addCell(cell(line.taxRatePercentage() + "%", NORMAL_FONT));
            table.addCell(cell(line.lineTotal().amount() + " " + currency, NORMAL_FONT));
        }
        doc.add(table);
    }

    private void addTotals(Document doc, Invoice invoice) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        String currency = invoice.getTotal().currency();
        Paragraph subtotal = new Paragraph("Base imponible: " + invoice.getSubtotal().amount() + " " + currency, NORMAL_FONT);
        subtotal.setAlignment(Element.ALIGN_RIGHT);
        doc.add(subtotal);

        Paragraph tax = new Paragraph("IVA: " + invoice.getTaxAmount().amount() + " " + currency, NORMAL_FONT);
        tax.setAlignment(Element.ALIGN_RIGHT);
        doc.add(tax);

        Paragraph total = new Paragraph("TOTAL: " + invoice.getTotal().amount() + " " + currency, LABEL_FONT);
        total.setAlignment(Element.ALIGN_RIGHT);
        doc.add(total);
    }

    private void addFooter(Document doc) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        doc.add(new LineSeparator());
        Paragraph footer = new Paragraph("Generado por Gresk", SMALL_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell c = new PdfPCell(new Phrase(h, LABEL_FONT));
            c.setBackgroundColor(TABLE_BG);
            c.setPadding(4);
            table.addCell(c);
        }
    }

    private PdfPCell cell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text != null ? text : "", font));
        c.setPadding(4);
        c.setBorderWidth(0.5f);
        return c;
    }
}
