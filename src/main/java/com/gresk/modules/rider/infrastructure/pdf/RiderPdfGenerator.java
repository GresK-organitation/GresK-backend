package com.gresk.modules.rider.infrastructure.pdf;

import com.gresk.modules.rider.domain.model.RiderLineItem;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.model.valueobject.StageDimensions;
import com.gresk.modules.rider.domain.model.valueobject.StageElement;
import com.gresk.modules.rider.domain.model.valueobject.StaffMember;
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
public class RiderPdfGenerator {

    private static final Font TITLE_FONT   = new Font(Font.HELVETICA, 20, Font.BOLD, Color.BLACK);
    private static final Font HEADING_FONT = new Font(Font.HELVETICA, 13, Font.BOLD, new Color(30, 80, 160));
    private static final Font LABEL_FONT   = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);
    private static final Font NORMAL_FONT  = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
    private static final Font SMALL_FONT   = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);
    private static final Color TABLE_HEADER_BG = new Color(220, 230, 245);

    public byte[] generate(TechnicalRider rider, String artistName) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, rider, artistName);
            addSection(doc, "1. Personal");
            addStaffTable(doc, rider.getStaff());
            addSection(doc, "2. Prueba de sonido (Sound check)");
            addSoundCheck(doc, rider);
            addSection(doc, "3. Escenario (Stage)");
            addStageDimensions(doc, rider.getStageDimensions());
            addStagePlot(doc, rider.getStageElements());
            addSection(doc, "4. Especificaciones técnicas y BOM");
            addLineItemsTable(doc, rider.getLineItems());
            if (rider.getAdditionalNotes() != null && !rider.getAdditionalNotes().isBlank()) {
                addSection(doc, "5. Notas adicionales");
                doc.add(new Paragraph(rider.getAdditionalNotes(), NORMAL_FONT));
            }

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating rider PDF for rider {}: {}", rider.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate rider PDF", e);
        }
    }

    // ── Sections ─────────────────────────────────────────────────────────────

    private void addHeader(Document doc, TechnicalRider rider, String artistName) throws DocumentException {
        Paragraph title = new Paragraph(artistName != null ? artistName : "Rider Técnico", TITLE_FONT);
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
    }

    private void addSection(Document doc, String title) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        Paragraph h = new Paragraph(title, HEADING_FONT);
        doc.add(h);
        doc.add(new com.lowagie.text.pdf.draw.LineSeparator());
        doc.add(Chunk.NEWLINE);
    }

    private void addStaffTable(Document doc, List<StaffMember> staff) throws DocumentException {
        if (staff == null || staff.isEmpty()) {
            doc.add(new Paragraph("No especificado", SMALL_FONT));
            return;
        }
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        table.setWidths(new float[]{1f, 2f});
        addTableHeader(table, "Rol", "Nombre");
        for (StaffMember s : staff) {
            table.addCell(cell(s.role(), NORMAL_FONT));
            table.addCell(cell(s.name(), LABEL_FONT));
        }
        doc.add(table);
    }

    private void addSoundCheck(Document doc, TechnicalRider rider) throws DocumentException {
        if (rider.getSoundCheckDurationMinutes() != null) {
            doc.add(new Paragraph("Duración mínima: " + rider.getSoundCheckDurationMinutes() + " minutos", NORMAL_FONT));
        }
        if (rider.getSoundCheckNotes() != null && !rider.getSoundCheckNotes().isBlank()) {
            doc.add(new Paragraph(rider.getSoundCheckNotes(), NORMAL_FONT));
        }
        if (rider.getSoundCheckDurationMinutes() == null && (rider.getSoundCheckNotes() == null || rider.getSoundCheckNotes().isBlank())) {
            doc.add(new Paragraph("No especificado", SMALL_FONT));
        }
    }

    private void addStageDimensions(Document doc, StageDimensions sd) throws DocumentException {
        if (sd == null) { doc.add(new Paragraph("Sin dimensiones especificadas", SMALL_FONT)); return; }
        if (sd.widthMeters() != null)   doc.add(new Paragraph("Ancho: " + sd.widthMeters() + " m", NORMAL_FONT));
        if (sd.depthMeters() != null)   doc.add(new Paragraph("Fondo: " + sd.depthMeters() + " m", NORMAL_FONT));
        if (sd.minHeightMeters() != null) doc.add(new Paragraph("Altura mínima: " + sd.minHeightMeters() + " m", NORMAL_FONT));
        if (sd.powerOutlets() != null)  doc.add(new Paragraph("Tomas de corriente: " + sd.powerOutlets(), NORMAL_FONT));
        doc.add(new Paragraph("Tarima de batería: " + (sd.hasDrumRiser() ? "Sí" : "No"), NORMAL_FONT));
    }

    private void addStagePlot(Document doc, List<StageElement> elements) throws DocumentException {
        if (elements == null || elements.isEmpty()) {
            doc.add(new Paragraph("Stage plot no proporcionado.", SMALL_FONT));
            return;
        }
        doc.add(new Paragraph("Elementos en escenario:", LABEL_FONT));
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(90);
        table.setWidths(new float[]{2f, 1.5f, 1.5f, 1f});
        addTableHeader(table, "Label", "Tipo", "Pos. X%", "Pos. Y%");
        for (StageElement el : elements) {
            table.addCell(cell(nvl(el.label()), NORMAL_FONT));
            table.addCell(cell(el.type().name(), SMALL_FONT));
            table.addCell(cell(String.format("%.0f%%", el.xPercent()), NORMAL_FONT));
            table.addCell(cell(String.format("%.0f%%", el.yPercent()), NORMAL_FONT));
        }
        doc.add(table);
        doc.add(new Paragraph("* El plano de escenario visual está disponible en la app Gresk.", SMALL_FONT));
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

    // ── Helpers ───────────────────────────────────────────────────────────────

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

    private String nvl(String v) {
        return v != null ? v : "";
    }
}
