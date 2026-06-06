package com.gresk.modules.contract.infrastructure.pdf;

import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.model.valueobject.ContractClause;
import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.contract.domain.model.valueobject.FinancialTerms;
import com.gresk.modules.contract.domain.model.valueobject.PerformanceDetails;
import com.gresk.modules.contract.domain.model.valueobject.PaymentTerm;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class ContractPdfGenerator {

    private static final Font TITLE_FONT    = new Font(Font.HELVETICA, 18, Font.BOLD,  Color.BLACK);
    private static final Font HEADING_FONT  = new Font(Font.HELVETICA, 12, Font.BOLD,  new Color(30, 80, 160));
    private static final Font LABEL_FONT    = new Font(Font.HELVETICA, 10, Font.BOLD,  Color.BLACK);
    private static final Font NORMAL_FONT   = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
    private static final Font CLAUSE_FONT   = new Font(Font.HELVETICA, 9,  Font.NORMAL, Color.BLACK);
    private static final Font SMALL_FONT    = new Font(Font.HELVETICA, 8,  Font.NORMAL, Color.DARK_GRAY);
    private static final Color TABLE_BG     = new Color(220, 230, 245);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generate(Contract contract) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, contract);
            addSectionTitle(doc, "PARTES CONTRATANTES");
            addPartiesTable(doc, contract.getPartyA(), contract.getPartyB());

            if (contract.getPerformanceDetails() != null) {
                addSectionTitle(doc, "DETALLES DE LA ACTUACIÓN");
                addPerformanceDetails(doc, contract.getPerformanceDetails());
            }

            if (contract.getFinancialTerms() != null) {
                addSectionTitle(doc, "CONDICIONES ECONÓMICAS");
                addFinancialTerms(doc, contract.getFinancialTerms());
            }

            if (!contract.getClauses().isEmpty()) {
                addSectionTitle(doc, "CLÁUSULAS");
                addClauses(doc, contract.getClauses());
            }

            addSignatureBlock(doc, contract);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating contract PDF for {}: {}", contract.getReferenceNumber(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate contract PDF", e);
        }
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    private void addHeader(Document doc, Contract contract) throws DocumentException {
        Paragraph title = new Paragraph("CONTRATO DE " + typeLabel(contract.getType()), TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        Paragraph ref = new Paragraph("Ref: " + contract.getReferenceNumber(), HEADING_FONT);
        ref.setAlignment(Element.ALIGN_CENTER);
        doc.add(ref);

        if (contract.getContractDate() != null) {
            Paragraph date = new Paragraph(
                    formatLocation(contract.getContractCity(), contract.getContractDate()), SMALL_FONT);
            date.setAlignment(Element.ALIGN_CENTER);
            doc.add(date);
        }
        doc.add(Chunk.NEWLINE);
        doc.add(new LineSeparator());
        doc.add(Chunk.NEWLINE);
    }

    private void addSectionTitle(Document doc, String title) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        Paragraph h = new Paragraph(title, HEADING_FONT);
        doc.add(h);
        doc.add(new LineSeparator());
        doc.add(Chunk.NEWLINE);
    }

    private void addPartiesTable(Document doc, ContractParty partyA, ContractParty partyB) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 1f});

        PdfPCell headerA = headerCell("PARTE A");
        PdfPCell headerB = headerCell("PARTE B");
        table.addCell(headerA);
        table.addCell(headerB);

        table.addCell(partyCell(partyA));
        table.addCell(partyCell(partyB));

        doc.add(table);
    }

    private void addPerformanceDetails(Document doc, PerformanceDetails pd) throws DocumentException {
        if (pd.venue() != null)           doc.add(new Paragraph("Recinto: " + pd.venue(), NORMAL_FONT));
        if (pd.eventDate() != null)       doc.add(new Paragraph("Fecha: " + pd.eventDate().format(DATE_FMT), NORMAL_FONT));
        if (pd.showTime() != null)        doc.add(new Paragraph("Horario: " + pd.showTime(), NORMAL_FONT));
        if (pd.durationMinutes() != null) doc.add(new Paragraph("Duración aproximada: " + pd.durationMinutes() + " minutos", NORMAL_FONT));
    }

    private void addFinancialTerms(Document doc, FinancialTerms ft) throws DocumentException {
        doc.add(new Paragraph("Honorarios: " + ft.feeAmount() + " " + ft.feeCurrency(), LABEL_FONT));
        doc.add(Chunk.NEWLINE);

        if (ft.paymentTerms() != null && !ft.paymentTerms().isEmpty()) {
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(90);
            table.setWidths(new float[]{3f, 1.5f, 2f, 1f});
            addTableHeader(table, "Concepto", "%", "Método", "Pag.");
            for (PaymentTerm pt : ft.paymentTerms()) {
                table.addCell(cell(pt.description(), NORMAL_FONT));
                table.addCell(cell(pt.percentage() + "%", NORMAL_FONT));
                table.addCell(cell(pt.method() != null ? pt.method() : "", NORMAL_FONT));
                table.addCell(cell(pt.paid() ? "✓" : "✗", LABEL_FONT));
            }
            doc.add(table);
        }
    }

    private void addClauses(Document doc, List<ContractClause> clauses) throws DocumentException {
        for (ContractClause clause : clauses) {
            Paragraph clauseTitle = new Paragraph(clause.order() + ". " + clause.title(), LABEL_FONT);
            clauseTitle.setSpacingBefore(6);
            doc.add(clauseTitle);
            Paragraph content = new Paragraph(clause.content(), CLAUSE_FONT);
            content.setIndentationLeft(12);
            doc.add(content);
        }
    }

    private void addSignatureBlock(Document doc, Contract contract) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        doc.add(new LineSeparator());
        doc.add(Chunk.NEWLINE);

        String lugar = contract.getContractCity() != null ? contract.getContractCity() : "_______________";
        String fecha = contract.getContractDate() != null
                ? contract.getContractDate().format(DATE_FMT) : "_______________";
        doc.add(new Paragraph("En " + lugar + ", a " + fecha, NORMAL_FONT));

        if (contract.getJurisdiction() != null) {
            Paragraph jur = new Paragraph("Jurisdicción: " + contract.getJurisdiction(), SMALL_FONT);
            doc.add(jur);
        }
        doc.add(Chunk.NEWLINE);

        PdfPTable sigTable = new PdfPTable(2);
        sigTable.setWidthPercentage(100);
        sigTable.setWidths(new float[]{1f, 1f});
        sigTable.addCell(signatureCell("PARTE A", contract.getPartyA()));
        sigTable.addCell(signatureCell("PARTE B", contract.getPartyB()));
        doc.add(sigTable);

        doc.add(Chunk.NEWLINE);
        Paragraph footer = new Paragraph(
                "Generado por Gresk · " + LocalDate.now().format(DATE_FMT), SMALL_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String typeLabel(ContractType type) {
        return switch (type) {
            case PERFORMANCE    -> "ACTUACIÓN EN DIRECTO";
            case REPRESENTATION -> "REPRESENTACIÓN ARTÍSTICA";
            case TICKETING      -> "VENTA DE ENTRADAS";
            case PRIVATE_FESTIVAL -> "FESTIVAL PRIVADO";
        };
    }

    private String formatLocation(String city, LocalDate date) {
        String c = city != null ? city : "";
        String d = date != null ? date.format(DATE_FMT) : "";
        if (!c.isBlank() && !d.isBlank()) return c + ", a " + d;
        return c + d;
    }

    private PdfPCell headerCell(String label) {
        PdfPCell c = new PdfPCell(new Phrase(label, LABEL_FONT));
        c.setBackgroundColor(TABLE_BG);
        c.setPadding(6);
        return c;
    }

    private PdfPCell partyCell(ContractParty party) {
        StringBuilder sb = new StringBuilder();
        if (party != null) {
            if (party.name()          != null) sb.append(party.name()).append("\n");
            if (party.taxId()         != null) sb.append("CIF/NIF: ").append(party.taxId()).append("\n");
            if (party.address()       != null) sb.append(party.address()).append("\n");
            if (party.signatoryName() != null) sb.append("Representado por: ").append(party.signatoryName());
            if (party.signatoryRole() != null) sb.append(" (").append(party.signatoryRole()).append(")");
        }
        PdfPCell c = new PdfPCell(new Phrase(sb.toString(), NORMAL_FONT));
        c.setPadding(6);
        c.setMinimumHeight(60);
        return c;
    }

    private PdfPCell signatureCell(String label, ContractParty party) {
        StringBuilder sb = new StringBuilder(label).append("\n\n");
        if (party != null && party.signatoryName() != null) {
            sb.append(party.signatoryName()).append("\n");
            if (party.signatoryRole() != null) sb.append(party.signatoryRole()).append("\n");
        }
        sb.append("\n\n___________________________\nFirma y sello");
        PdfPCell c = new PdfPCell(new Phrase(sb.toString(), NORMAL_FONT));
        c.setPadding(10);
        c.setMinimumHeight(100);
        return c;
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
