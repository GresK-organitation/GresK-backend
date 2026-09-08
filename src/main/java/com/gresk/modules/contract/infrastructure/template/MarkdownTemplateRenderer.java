package com.gresk.modules.contract.infrastructure.template;

import com.gresk.modules.contract.domain.port.out.TemplateRenderingPort;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pipeline nuevo y separado del ContractPdfGenerator (OpenPDF) existente:
 * Markdown con {{variable.path}} -> interpolación -> HTML (commonmark) -> XHTML
 * bien formado (jsoup) -> PDF (Flying Saucer, sobre el mismo backend OpenPDF).
 */
@Slf4j
@Component
public class MarkdownTemplateRenderer implements TemplateRenderingPort {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*([\\w.]+)\\s*\\}\\}");

    private static final String CSS = """
            body { font-family: Helvetica, Arial, sans-serif; font-size: 11px; color: #111; }
            h1 { font-size: 18px; text-align: center; }
            h2 { font-size: 13px; color: #1e50a0; border-bottom: 1px solid #ccc; padding-bottom: 4px; }
            hr { border: none; border-top: 1px solid #ccc; }
            """;

    @Override
    public byte[] renderToPdf(String bodyMarkdown, Map<String, Object> variables) {
        String interpolated = interpolate(bodyMarkdown, variables);
        String html = markdownToHtml(interpolated);
        String xhtml = toWellFormedXhtml(html);
        return renderPdf(xhtml);
    }

    // ── Interpolación ─────────────────────────────────────────────────────────

    String interpolate(String template, Map<String, Object> variables) {
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String path = matcher.group(1);
            Object value = resolvePath(variables, path);
            matcher.appendReplacement(result, Matcher.quoteReplacement(value != null ? value.toString() : ""));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    @SuppressWarnings("unchecked")
    private Object resolvePath(Map<String, Object> root, String path) {
        String[] segments = path.split("\\.");
        Object current = root;
        for (String segment : segments) {
            if (!(current instanceof Map<?, ?> map)) return null;
            current = ((Map<String, Object>) map).get(segment);
            if (current == null) return null;
        }
        return current;
    }

    // ── Markdown -> HTML -> XHTML -> PDF ─────────────────────────────────────

    private String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        return HtmlRenderer.builder().build().render(document);
    }

    private String toWellFormedXhtml(String html) {
        String wrapped = "<html><head><style>" + CSS + "</style></head><body>" + html + "</body></html>";
        Document doc = Jsoup.parse(wrapped);
        doc.outputSettings()
                .syntax(Document.OutputSettings.Syntax.xml)
                .escapeMode(Entities.EscapeMode.xhtml);
        return doc.html();
    }

    private byte[] renderPdf(String xhtml) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(xhtml);
            renderer.layout();
            renderer.createPDF(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error rendering contract template to PDF: {}", e.getMessage(), e);
            throw new TemplateRenderingException("Failed to render contract template PDF", e);
        }
    }
}
