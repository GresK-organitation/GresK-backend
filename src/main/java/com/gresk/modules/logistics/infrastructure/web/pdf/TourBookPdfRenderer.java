package com.gresk.modules.logistics.infrastructure.web.pdf;

import com.gresk.modules.logistics.application.dto.TourBookResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Compila el TourBookResponse a PDF vía Flying Saucer/OpenPDF, el mismo backend que
 * contract.MarkdownTemplateRenderer pero sin capa de plantillas: el contenido ya
 * llega estructurado desde GenerateTourBookUseCase.
 */
@Component
public class TourBookPdfRenderer {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private static final String CSS = """
            body { font-family: Helvetica, Arial, sans-serif; font-size: 11px; color: #111; }
            h1 { font-size: 20px; text-align: center; margin-bottom: 2px; }
            h2 { font-size: 15px; color: #1e50a0; border-bottom: 1px solid #ccc; padding-bottom: 4px; margin-top: 18px; }
            h3 { font-size: 12px; margin-bottom: 2px; }
            table { width: 100%; border-collapse: collapse; margin-bottom: 8px; }
            td, th { border: 1px solid #ccc; padding: 3px 6px; text-align: left; font-size: 10px; }
            .subtitle { text-align: center; color: #555; margin-bottom: 12px; }
            """;

    public byte[] render(TourBookResponse tourBook) {
        return renderPdf(buildXhtml(tourBook));
    }

    private String buildXhtml(TourBookResponse t) {
        StringBuilder body = new StringBuilder();
        body.append("<h1>").append(nz(t.tourName())).append("</h1>");
        body.append("<div class=\"subtitle\">").append(DATE.format(t.startDate())).append(" - ")
                .append(DATE.format(t.endDate())).append("</div>");

        if (!t.documentAlerts().isEmpty()) {
            body.append("<h2>Avisos de documentación</h2><table><tr><th>Persona</th><th>Documento</th><th>Caduca</th></tr>");
            for (var a : t.documentAlerts()) {
                body.append("<tr><td>").append(nz(a.travelerName())).append("</td><td>")
                        .append(nz(a.documentType())).append("</td><td>")
                        .append(DATE.format(a.expiryDate())).append("</td></tr>");
            }
            body.append("</table>");
        }

        for (var day : t.days()) {
            body.append("<h2>").append(DATE.format(day.date())).append(" — ")
                    .append(nz(day.venueName())).append(", ").append(nz(day.venueCity())).append("</h2>");

            if (day.hotel() != null) {
                body.append("<h3>Hotel</h3><p>").append(nz(day.hotel().hotelName()));
                if (day.hotel().hotelAddress() != null) body.append(" — ").append(nz(day.hotel().hotelAddress()));
                body.append("</p>");
            }

            if (!day.schedule().isEmpty()) {
                body.append("<h3>Horario</h3><table><tr><th>Hora</th><th>Tipo</th><th>Detalle</th></tr>");
                for (var line : day.schedule()) {
                    body.append("<tr><td>").append(TIME.format(line.time())).append("</td><td>")
                            .append(nz(line.type())).append("</td><td>").append(nz(line.label()));
                    if (line.notes() != null) body.append(" (").append(nz(line.notes())).append(")");
                    body.append("</td></tr>");
                }
                body.append("</table>");
            }

            if (!day.transport().isEmpty()) {
                body.append("<h3>Transporte</h3><table><tr><th>Tipo</th><th>Salida</th><th>Llegada</th><th>Operador</th><th>Localizador</th></tr>");
                for (var line : day.transport()) {
                    body.append("<tr><td>").append(nz(line.type())).append("</td><td>")
                            .append(nz(line.departureLocation())).append("</td><td>")
                            .append(nz(line.arrivalLocation())).append("</td><td>")
                            .append(nz(line.carrierOrOperator())).append("</td><td>")
                            .append(nz(line.confirmationReference())).append("</td></tr>");
                }
                body.append("</table>");
            }
        }

        if (!t.emergencyContacts().isEmpty()) {
            body.append("<h2>Contactos de emergencia</h2><table><tr><th>Nombre</th><th>Rol</th><th>Teléfono</th></tr>");
            for (var c : t.emergencyContacts()) {
                body.append("<tr><td>").append(nz(c.name())).append("</td><td>")
                        .append(nz(c.role())).append("</td><td>").append(nz(c.phone())).append("</td></tr>");
            }
            body.append("</table>");
        }

        if (!t.pointsOfInterest().isEmpty()) {
            body.append("<h2>Restaurantes y otros puntos de interés</h2><table><tr><th>Nombre</th><th>Categoría</th><th>Dirección</th></tr>");
            for (var p : t.pointsOfInterest()) {
                body.append("<tr><td>").append(nz(p.name())).append("</td><td>")
                        .append(nz(p.category())).append("</td><td>").append(nz(p.address())).append("</td></tr>");
            }
            body.append("</table>");
        }

        String wrapped = "<html><head><style>" + CSS + "</style></head><body>" + body + "</body></html>";
        Document doc = Jsoup.parse(wrapped);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml).escapeMode(Entities.EscapeMode.xhtml);
        return doc.html();
    }

    private String nz(String value) {
        return value == null ? "" : value;
    }

    private byte[] renderPdf(String xhtml) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(xhtml);
            renderer.layout();
            renderer.createPDF(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to render Tour Book PDF: " + e.getMessage(), e);
        }
    }
}
