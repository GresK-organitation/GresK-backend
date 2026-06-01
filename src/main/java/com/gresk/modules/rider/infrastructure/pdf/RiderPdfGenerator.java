package com.gresk.modules.rider.infrastructure.pdf;

import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.model.valueobject.*;
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
            addSection(doc, "3. Lista de canales (Input list)");
            addInputChannelsTable(doc, rider.getInputChannels());
            addSection(doc, "4. Sistema de sonido (FOH)");
            addSoundSystem(doc, rider.getSoundSystem());
            addSection(doc, "5. Backline");
            addBacklineTable(doc, rider.getBacklineItems());
            addSection(doc, "6. Escenario (Stage)");
            addStageDimensions(doc, rider.getStageDimensions());
            addStagePlot(doc, rider.getStageElements(), writer -> {});
            addSection(doc, "7. Hospitalidad");
            addHospitality(doc, rider.getHospitality());
            addSection(doc, "8. Transporte");
            addTransport(doc, rider.getTransport());
            if (rider.getAdditionalNotes() != null && !rider.getAdditionalNotes().isBlank()) {
                addSection(doc, "9. Notas adicionales");
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

    private void addInputChannelsTable(Document doc, List<InputChannel> channels) throws DocumentException {
        if (channels == null || channels.isEmpty()) {
            doc.add(new Paragraph("Sin lista de canales", SMALL_FONT));
            return;
        }
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.5f, 2f, 2f, 2f, 2f});
        addTableHeader(table, "Ch", "Instrumento", "Micrófono", "Inserts", "Notas");
        for (InputChannel c : channels) {
            table.addCell(cell(String.valueOf(c.channelNumber()), NORMAL_FONT));
            table.addCell(cell(c.instrument(), NORMAL_FONT));
            table.addCell(cell(nvl(c.microphone()), NORMAL_FONT));
            table.addCell(cell(nvl(c.inserts()), NORMAL_FONT));
            table.addCell(cell(nvl(c.notes()), SMALL_FONT));
        }
        doc.add(table);
    }

    private void addSoundSystem(Document doc, SoundSystemRequirements ss) throws DocumentException {
        if (ss == null) { doc.add(new Paragraph("No especificado", SMALL_FONT)); return; }
        if (ss.consoleBrand() != null)   doc.add(new Paragraph("Mesa: " + ss.consoleBrand() + (ss.consoleChannels() != null ? " — " + ss.consoleChannels() + " canales" : ""), NORMAL_FONT));
        if (ss.monitorMixes() != null)   doc.add(new Paragraph("Mezclas de monitor: " + ss.monitorMixes(), NORMAL_FONT));
        if (ss.paDescription() != null && !ss.paDescription().isBlank()) doc.add(new Paragraph("PA: " + ss.paDescription(), NORMAL_FONT));
        if (ss.processorNotes() != null && !ss.processorNotes().isBlank()) doc.add(new Paragraph("Procesadores: " + ss.processorNotes(), NORMAL_FONT));
    }

    private void addBacklineTable(Document doc, List<BacklineItem> items) throws DocumentException {
        if (items == null || items.isEmpty()) {
            doc.add(new Paragraph("Sin backline especificado", SMALL_FONT));
            return;
        }
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.5f, 3f, 1.5f, 1.5f, 0.8f});
        addTableHeader(table, "Categoría", "Descripción", "Marca", "Modelo", "Req.");
        for (BacklineItem b : items) {
            table.addCell(cell(b.category().name(), SMALL_FONT));
            table.addCell(cell(b.description(), NORMAL_FONT));
            table.addCell(cell(nvl(b.brand()), NORMAL_FONT));
            table.addCell(cell(nvl(b.model()), NORMAL_FONT));
            table.addCell(cell(b.required() ? "✓" : "", LABEL_FONT));
        }
        doc.add(table);
    }

    private void addStageDimensions(Document doc, StageDimensions sd) throws DocumentException {
        if (sd == null) { doc.add(new Paragraph("Sin dimensiones especificadas", SMALL_FONT)); return; }
        if (sd.widthMeters() != null)   doc.add(new Paragraph("Ancho: " + sd.widthMeters() + " m", NORMAL_FONT));
        if (sd.depthMeters() != null)   doc.add(new Paragraph("Fondo: " + sd.depthMeters() + " m", NORMAL_FONT));
        if (sd.minHeightMeters() != null) doc.add(new Paragraph("Altura mínima: " + sd.minHeightMeters() + " m", NORMAL_FONT));
        if (sd.powerOutlets() != null)  doc.add(new Paragraph("Tomas de corriente: " + sd.powerOutlets(), NORMAL_FONT));
        doc.add(new Paragraph("Tarima de batería: " + (sd.hasDrumRiser() ? "Sí" : "No"), NORMAL_FONT));
    }

    private void addStagePlot(Document doc, List<StageElement> elements, java.util.function.Consumer<PdfWriter> ignored)
            throws DocumentException {
        if (elements == null || elements.isEmpty()) {
            doc.add(new Paragraph("Stage plot no proporcionado.", SMALL_FONT));
            return;
        }
        // Stage plot as a simple labeled item list (frontend handles the visual canvas)
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

    private void addHospitality(Document doc, HospitalityRequirements h) throws DocumentException {
        if (h == null) { doc.add(new Paragraph("Sin especificar", SMALL_FONT)); return; }
        if (h.dressingRoomCapacity() != null) doc.add(new Paragraph("Camerino para: " + h.dressingRoomCapacity() + " personas", NORMAL_FONT));
        if (h.waterBottlesOnStage() != null && h.waterBottlesOnStage() > 0) doc.add(new Paragraph("Agua en escenario: " + h.waterBottlesOnStage() + " botellas", NORMAL_FONT));
        if (h.cateringNotes() != null && !h.cateringNotes().isBlank()) doc.add(new Paragraph("Catering: " + h.cateringNotes(), NORMAL_FONT));
        if (h.passesCount() != null && h.passesCount() > 0) doc.add(new Paragraph("Acreditaciones: " + h.passesCount(), NORMAL_FONT));
    }

    private void addTransport(Document doc, TransportRequirements t) throws DocumentException {
        if (t == null || t.vehicleType() == null) { doc.add(new Paragraph("Sin especificar", SMALL_FONT)); return; }
        String desc = t.vehicleType();
        if (t.passengerCapacity() != null) desc += " para " + t.passengerCapacity() + " personas";
        doc.add(new Paragraph(desc, NORMAL_FONT));
        if (t.notes() != null && !t.notes().isBlank()) doc.add(new Paragraph(t.notes(), NORMAL_FONT));
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
