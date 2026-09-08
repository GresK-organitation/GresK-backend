package com.gresk.modules.rider.infrastructure.pdf;

import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.model.RiderLineItem;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class HospitalityRiderPdfGenerator {

    private static final Font TITLE_FONT   = new Font(Font.HELVETICA, 20, Font.BOLD, Color.BLACK);
    private static final Font HEADING_FONT = new Font(Font.HELVETICA, 13, Font.BOLD, new Color(30, 80, 160));
    private static final Font LABEL_FONT   = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);
    private static final Font NORMAL_FONT  = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
    private static final Font SMALL_FONT   = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);
    private static final Color TABLE_HEADER_BG = new Color(220, 230, 245);

    public byte[] generate(HospitalityRider rider, String artistName) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Paragraph title = new Paragraph(artistName != null ? artistName : "Rider Hospitality", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            Paragraph subtitle = new Paragraph(rider.getName() + "  ·  v" + rider.getVersion(), HEADING_FONT);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            doc.add(subtitle);

            String date = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    .withZone(ZoneOffset.UTC)
                    .format(rider.getUpdatedAt());
            Paragraph dateP = new Paragraph("Actualizado: " + date, SMALL_FONT);
            dateP.setAlignment(Element.ALIGN_CENTER);
            doc.add(dateP);
            doc.add(Chunk.NEWLINE);
            doc.add(new com.lowagie.text.pdf.draw.LineSeparator());
            doc.add(Chunk.NEWLINE);

            doc.add(new Paragraph("Especificaciones de hospitality", HEADING_FONT));
            doc.add(new com.lowagie.text.pdf.draw.LineSeparator());
            doc.add(Chunk.NEWLINE);
            addLineItemsTable(doc, rider.getLineItems());

            if (rider.getAdditionalNotes() != null && !rider.getAdditionalNotes().isBlank()) {
                doc.add(Chunk.NEWLINE);
                doc.add(new Paragraph("Notas adicionales", HEADING_FONT));
                doc.add(new Paragraph(rider.getAdditionalNotes(), NORMAL_FONT));
            }

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating hospitality rider PDF for rider {}: {}", rider.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate hospitality rider PDF", e);
        }
    }

    private void addLineItemsTable(Document doc, List<RiderLineItem> items) throws DocumentException {
        if (items == null || items.isEmpty()) {
            doc.add(new Paragraph("Sin ítems especificados", SMALL_FONT));
            return;
        }
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.3f, 3f, 0.6f, 0.6f, 1.3f, 2f});
        addTableHeader(table, "Categoría", "Descripción", "Cant.", "Req.", "Fulfillment", "Atributos");
        for (RiderLineItem item : items) {
            table.addCell(cell(item.getCategory().name(), SMALL_FONT));
            table.addCell(cell(item.getDescription(), NORMAL_FONT));
            table.addCell(cell(String.valueOf(item.getQuantity()), NORMAL_FONT));
            table.addCell(cell(item.isRequired() ? "✓" : "", LABEL_FONT));
            table.addCell(cell(item.getFulfillmentSource().name(), SMALL_FONT));
            table.addCell(cell(formatAttributes(item.getAttributes()), SMALL_FONT));
        }
        doc.add(table);
    }

    private String formatAttributes(Map<String, String> attributes) {
        if (attributes == null || attributes.isEmpty()) return "";
        return attributes.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .reduce((a, b) -> a + " · " + b)
                .orElse("");
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell c = new PdfPCell(new Phrase(h, LABEL_FONT));
            c.setBackgroundColor(TABLE_HEADER_BG);
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
